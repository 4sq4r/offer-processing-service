package kz.offerprocessservice.processor;

import kz.offerprocessservice.model.dto.PriceListItemDTO;
import kz.offerprocessservice.model.entity.OfferEntity;
import kz.offerprocessservice.model.entity.StockEntity;
import kz.offerprocessservice.model.entity.WarehouseEntity;
import kz.offerprocessservice.service.StockService;
import kz.offerprocessservice.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StockProcessor {

    private final StockService stockService;
    private final WarehouseService warehouseService;

    public void saveStocks(
            Set<PriceListItemDTO> priceListItemDTOSet,
            Set<OfferEntity> offerEntitySet,
            String merchantId
    ) {
        Map<String, WarehouseEntity> warehouseEntityMap = warehouseService.getAllWarehousesByMerchantId(merchantId)
                .stream()
                .collect(Collectors.toMap(WarehouseEntity::getId, wh -> wh));
        Set<StockEntity> stockEntitySet = new HashSet<>();
        priceListItemDTOSet.forEach(item -> {
            OfferEntity offerEntity = offerEntitySet.stream()
                    .filter(offer -> offer.getOfferCode().equals(item.getOfferCode()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Offer not found after saving"));
            Map<String, Integer> stockMap = item.getStocks();
            stockMap.forEach((warehouseName, stockValue) -> {
                WarehouseEntity warehouse = warehouseEntityMap.get(warehouseName);

                if (warehouse != null) {
                    StockEntity stock = new StockEntity();
                    stock.setStock(stockValue);
                    stock.setWarehouse(warehouse);
                    stock.setOffer(offerEntity);
                    stockEntitySet.add(stock);
                }
            });
        });

        if (!stockEntitySet.isEmpty()) {
            stockService.saveAll(stockEntitySet);
        }
    }
}
