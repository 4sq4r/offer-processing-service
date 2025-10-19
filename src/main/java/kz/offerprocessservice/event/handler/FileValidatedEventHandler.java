package kz.offerprocessservice.event.handler;

import kz.offerprocessservice.event.FileValidatedEvent;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.file.FileStrategyProviderImpl;
import kz.offerprocessservice.file.processing.FileProcessingStrategy;
import kz.offerprocessservice.model.PriceListState;
import kz.offerprocessservice.model.dto.PriceListItemDTO;
import kz.offerprocessservice.model.entity.*;
import kz.offerprocessservice.model.enums.OfferStatus;
import kz.offerprocessservice.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileValidatedEventHandler {

    private final OfferService offerService;
    private final StockService stockService;
    private final MerchantService merchantService;
    private final MinioService minioService;
    private final PriceListService priceListService;
    private final WarehouseService warehouseService;
    private final FileStrategyProviderImpl fileStrategyProvider;

    @Value("${minio.prefix-to-delete}")
    private String minioPrefixToDelete;

    @EventListener
    public void handle(FileValidatedEvent event) throws CustomException, IOException, URISyntaxException {
        log.info("Handling validated file event, price list id: {}", event.getPriceList().getId());
        PriceListEntity ple = event.getPriceList();
        ple.setStatus(PriceListState.PROCESSING);
        priceListService.updateState(ple.getId(), PriceListState.PROCESSING);
        parse(ple);
    }

    private void parse(PriceListEntity priceListEntity) throws CustomException, IOException, URISyntaxException {
        try {
            InputStream inputStream = minioService.getFile(priceListEntity.getUrl()
                    .replaceFirst(minioPrefixToDelete, ""));
            MerchantEntity me = merchantService.findEntityById(priceListEntity.getMerchant().getId());
            FileProcessingStrategy fileProcessingStrategy = fileStrategyProvider.getProcessingStrategy(priceListEntity.getFormat());
            Set<PriceListItemDTO> priceListItems = fileProcessingStrategy.extract(inputStream);

            if (!priceListItems.isEmpty()) {
                Set<OfferEntity> offers = saveOffers(priceListItems, me);

                if (!offers.isEmpty()) {
                    Map<String, WarehouseEntity> warehouseMap = warehouseService.getAllWarehousesByMerchantId(me.getId()).stream()
                            .collect(Collectors.toMap(WarehouseEntity::getName, wh -> wh));
                    saveStocks(priceListItems, offers, warehouseMap);
                }
            }

            updatePriceListStatus(priceListEntity, PriceListState.PROCESSED, null);
            log.info("Price list successfully parsed: {}", priceListEntity.getId());
        } catch (SAXException e) {
            handleParsingError(priceListEntity, e);
        }
    }

    private Set<OfferEntity> saveOffers(Set<PriceListItemDTO> priceListItemSet, MerchantEntity merchantEntity) {
        if (!priceListItemSet.isEmpty()) {
            log.info("Started parse offers from price list items. List items count: {}", priceListItemSet.size());
            Set<OfferEntity> offers = priceListItemSet.stream()
                    .map(item -> {
                        OfferEntity offer = new OfferEntity();
                        offer.setOfferName(item.getOfferName());
                        offer.setOfferCode(item.getOfferCode());
                        offer.setMerchant(merchantEntity);
                        offer.setStatus(OfferStatus.NEW);

                        return offer;
                    }).collect(Collectors.toSet());

            if (!offers.isEmpty()) {
                offerService.saveAll(offers);
                log.info("Finished parse offers from price list items. Parsed offers count: {}", offers.size());
            }

            return offers;
        }

        return new HashSet<>();
    }

    private void saveStocks(Set<PriceListItemDTO> priceListItemDTO,
                            Set<OfferEntity> offers,
                            Map<String, WarehouseEntity> warehouseMap) {
        log.info("Started parse stocks from price list items. List items count: {}", priceListItemDTO.size());
        Set<StockEntity> stocks = new HashSet<>();
        priceListItemDTO.forEach(item -> {
            OfferEntity offerEntity = offers.stream()
                    .filter(o -> o.getOfferCode().equals(item.getOfferCode()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Offer not found after saving"));
            Map<String, Integer> stockMap = item.getStocks();
            stockMap.forEach((warehouseName, stockValue) -> {
                WarehouseEntity warehouse = warehouseMap.get(warehouseName);

                if (warehouse != null) {
                    StockEntity stock = new StockEntity();
                    stock.setStock(stockValue);
                    stock.setWarehouse(warehouse);
                    stock.setOffer(offerEntity);
                    stocks.add(stock);
                }
            });
        });

        if (!stocks.isEmpty()) {
            stockService.saveAll(stocks);
            log.info("Finished parse stocks from price list items. Parsed stocks count: {}", stocks.size());
        }
    }

    private void handleParsingError(PriceListEntity priceListEntity, Exception e) throws CustomException {
        updatePriceListStatus(priceListEntity, PriceListState.PROCESSING_FAILED, e.toString());
    }

    private void updatePriceListStatus(PriceListEntity priceListEntity, PriceListState status, String failReason) throws CustomException {
        priceListEntity.setStatus(status);
        priceListEntity.setFailReason(failReason);
        priceListService.updateState(priceListEntity.getId(), status);
    }
}
