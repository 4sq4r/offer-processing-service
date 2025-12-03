package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.file.FileStrategyProviderImpl;
import kz.offerprocessservice.file.processing.FileProcessingStrategy;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListStatus;
import kz.offerprocessservice.model.dto.PriceListItemDTO;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.OfferEntity;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.model.entity.StockEntity;
import kz.offerprocessservice.model.entity.WarehouseEntity;
import kz.offerprocessservice.model.enums.OfferStatus;
import kz.offerprocessservice.service.MerchantService;
import kz.offerprocessservice.service.MinioService;
import kz.offerprocessservice.service.OfferService;
import kz.offerprocessservice.service.PriceListService;
import kz.offerprocessservice.service.StockService;
import kz.offerprocessservice.service.WarehouseService;
import kz.offerprocessservice.service.statemachine.action.ActionNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
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
@Component(ActionNames.START_PROCESSING)
@RequiredArgsConstructor
public class ProcessingAction extends PriceListAction {

    private final OfferService offerService;
    private final StockService stockService;
    private final MerchantService merchantService;
    private final PriceListService priceListService;
    private final WarehouseService warehouseService;
    private final FileStrategyProviderImpl fileStrategyProvider;
    private final MinioService minioService;

    @Override
    public void doExecute(String priceListId, StateContext<PriceListStatus, PriceListEvent> context) {
        PriceListEntity priceListEntity = priceListService.findEntityById(priceListId);
        priceListEntity.setStatus(PriceListStatus.PROCESSING);
        priceListService.updateOne(priceListEntity);
        parse(priceListEntity);
    }

    private void parse(PriceListEntity priceListEntity) {
        try {
            InputStream inputStream = minioService.getFile(priceListEntity.getUrl());
            MerchantEntity me = merchantService.findEntityById(priceListEntity.getMerchant().getId());
            FileProcessingStrategy fileProcessingStrategy = fileStrategyProvider.getProcessingStrategy(
                    priceListEntity.getFormat());
            Set<PriceListItemDTO> priceListItems = fileProcessingStrategy.extract(inputStream);

            if (!priceListItems.isEmpty()) {
                Set<OfferEntity> offers = saveOffers(priceListItems, me);

                if (!offers.isEmpty()) {
                    Map<String, WarehouseEntity> warehouseMap = warehouseService.getAllWarehousesByMerchantId(
                                    me.getId()).stream()
                            .collect(Collectors.toMap(WarehouseEntity::getName, wh -> wh));
                    saveStocks(priceListItems, offers, warehouseMap);
                }
            }

            updatePriceListStatus(priceListEntity, PriceListStatus.PROCESSED, null);
        } catch (SAXException e) {
            handleParsingError(priceListEntity, e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<OfferEntity> saveOffers(Set<PriceListItemDTO> priceListItemSet, MerchantEntity merchantEntity) {
        if (!priceListItemSet.isEmpty()) {
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
            }

            return offers;
        }

        return new HashSet<>();
    }

    private void saveStocks(
            Set<PriceListItemDTO> priceListItemDTO,
            Set<OfferEntity> offers,
            Map<String, WarehouseEntity> warehouseMap
    ) {
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
        }
    }

    private void handleParsingError(PriceListEntity priceListEntity, Exception e) {
        updatePriceListStatus(priceListEntity, PriceListStatus.PROCESSING_FAILED, e.toString());
    }

    private void updatePriceListStatus(PriceListEntity priceListEntity, PriceListStatus status, String failReason) {
        priceListEntity.setStatus(status);
        priceListEntity.setFailReason(failReason);
        priceListService.updateOne(priceListEntity);
    }
}
