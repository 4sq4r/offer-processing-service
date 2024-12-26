package kz.offerprocessservice.scheduler;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.dto.PriceListItemDTO;
import kz.offerprocessservice.model.entity.*;
import kz.offerprocessservice.model.enums.OfferStatus;
import kz.offerprocessservice.model.enums.PriceListStatus;
import kz.offerprocessservice.service.*;
import kz.offerprocessservice.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PriceListScheduler {

    private final OfferService offerService;
    private final StockService stockService;
    private final MerchantService merchantService;
    private final MinioService minioService;
    private final PriceListService priceListService;
    private final WarehouseService warehouseService;

    @Value("${minio.prefix-to-delete}")
    private String minioPrefixToDelete;


    private final ExecutorService validationExecutor = Executors.newFixedThreadPool(2);
    private final ExecutorService processingExecutor = Executors.newFixedThreadPool(2);
    private final BlockingQueue<PriceListEntity> validationQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<PriceListEntity> processingQueue = new LinkedBlockingQueue<>();

    @Scheduled(fixedRate = 30000)
    public void scheduleValidation() {
        Set<PriceListEntity> priceLists = priceListService.findNewPriceLists();

        if (!priceLists.isEmpty()) {
            System.out.println("Starting validation file.");
            for (PriceListEntity priceList : priceLists) {
                priceList.setStatus(PriceListStatus.VALIDATION_WAITING);
                priceListService.updateStatus(priceList);
                validationQueue.offer(priceList);
                System.out.println("Added price list to validation queue.");
            }

            processValidationQueue();
        }
    }

    private void processValidationQueue() {
        for (int i = 0; i < 2; i++) {
            validationExecutor.submit(() -> {
                while (true) {
                    try {
                        PriceListEntity priceList = validationQueue.take();
                        priceList.setStatus(PriceListStatus.ON_VALIDATION);
                        priceListService.updateStatus(priceList);
                        boolean validated = validate(priceList);
                        if (validated) {
                            processingQueue.offer(priceList);
                            System.out.println("Added price list to processing queue.");
                        }
                    } catch (InterruptedException | CustomException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
    }

    private boolean validate(PriceListEntity priceList) throws CustomException {
        Set<String> pos = warehouseService.getAllWarehouseNamesByMerchantId(priceList.getMerchantId());
        InputStream is = minioService.getFile(priceList.getUrl().replaceFirst(minioPrefixToDelete, ""));
        String failReason = null;
        PriceListStatus status;

        try {
            if (FileUtils.validatePriceList(is, pos)) {
                status = PriceListStatus.VALIDATED;
            } else {
                failReason = "Incorrect headers.";
                status = PriceListStatus.VALIDATION_FAILED;
            }
        } catch (Exception e) {
            failReason = "Unable to validate file: " + e.getMessage();
            status = PriceListStatus.VALIDATION_FAILED;
        }

        priceList.setStatus(status);
        priceList.setFailReason(failReason);
        priceListService.updateStatus(priceList);

        return status == PriceListStatus.VALIDATED;
    }

    @Scheduled(fixedRate = 60000)
    public void scheduleProcessing() {
        if (!processingQueue.isEmpty()) {
            System.out.println("Starting processing a price list.");
            processProcessingQueue();
        }
    }

    private void processProcessingQueue() {
        for (int i = 0; i < 2; i++) {
            processingExecutor.submit(() -> {
                while (true) {
                    try {
                        PriceListEntity ple = processingQueue.take();
                        ple.setStatus(PriceListStatus.PROCESSING);
                        priceListService.updateStatus(ple);
                        processFile(ple);
                    } catch (InterruptedException | CustomException | IOException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
    }

    private void processFile(PriceListEntity ple) throws CustomException, IOException {
        InputStream is = minioService.getFile(ple.getUrl()
                .replaceFirst(minioPrefixToDelete, ""));
        MerchantEntity merchantEntity = merchantService.findEntityById(ple.getMerchantId());
        processOffers(is, merchantEntity);
    }

    private void processOffers(InputStream is, MerchantEntity me) throws IOException {
        System.out.println("Starting extract items from file");
        Set<PriceListItemDTO> priceListItems = FileUtils.extractPriceListItems(is);

        if (!priceListItems.isEmpty()) {
            Set<OfferEntity> offers = collectOffers(priceListItems, me);

            if (!offers.isEmpty()) {
                Map<String, WarehouseEntity> warehouseMap = warehouseService.getAllWarehousesByMerchantId(me.getId()).stream()
                        .collect(Collectors.toMap(WarehouseEntity::getName, wh -> wh));
                collectStocks(priceListItems, offers, warehouseMap);
            }
        }
    }

    private Set<OfferEntity> collectOffers(Set<PriceListItemDTO> priceListItems, MerchantEntity me) {
        if (!priceListItems.isEmpty()) {
            Set<OfferEntity> offers = priceListItems.stream().map(item -> {
                OfferEntity offer = new OfferEntity();
                offer.setOfferName(item.getOfferName());
                offer.setOfferCode(item.getOfferCode());
                offer.setMerchant(me);
                offer.setStatus(OfferStatus.NEW);

                return offer;
            }).collect(Collectors.toSet());

            if (!offers.isEmpty()) {
                offerService.saveAll(offers);
            }

            return offers;
        }

        return new HashSet<>();
    }

    private void collectStocks(Set<PriceListItemDTO> priceListItems,
                                           Set<OfferEntity> offers,
                                           Map<String, WarehouseEntity> warehouseMap) {
        Set<StockEntity> stocks = new HashSet<>();
        priceListItems.forEach(item -> {
            OfferEntity offerEntity = offers.stream()
                    .filter(o -> o.getOfferCode().equals(item.getOfferCode()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Offer not found after saving"));
            Map<String, Integer> stockMap = item.getStocks();
            stockMap.forEach((warehouseName, stockValue) -> {
                WarehouseEntity wh = warehouseMap.get(warehouseName);

                if (wh != null) {
                    StockEntity stock = new StockEntity();
                    stock.setStock(stockValue);
                    stock.setWarehouse(wh);
                    stock.setOffer(offerEntity);
                    stocks.add(stock);
                }
            });
        });

        if (!stocks.isEmpty()) {
            stockService.saveAll(stocks);
        }
    }


}
