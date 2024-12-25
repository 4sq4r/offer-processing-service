package kz.offerprocessservice.scheduler;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.OfferEntity;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.model.enums.PriceListStatus;
import kz.offerprocessservice.service.*;
import kz.offerprocessservice.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
public class PriceListScheduler {

    @Value("${minio.prefix-to-delete}")
    private String minioPrefixToDelete;

    private final OfferService offerService;
    private final MerchantService merchantService;
    private final MinioService minioService;
    private final PriceListService priceListService;
    private final WarehouseService warehouseService;
    private final ExecutorService validationExecutor = Executors.newFixedThreadPool(2);
    private final ExecutorService processingExecutor = Executors.newFixedThreadPool(2);
    private final BlockingQueue<PriceListEntity> validationQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<PriceListEntity> processingQueue = new LinkedBlockingQueue<>();

    @Scheduled(fixedRate = 60000)
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
        Set<String> pos = warehouseService.getAllPosNames(priceList.getMerchantId());
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
        Set<OfferEntity> set = FileUtils.extractOffers(is, me);
        if (!set.isEmpty()) {
            offerService.saveAll(set);
        }
    }
}
