package kz.offerprocessservice.processor;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.entity.CityEntity;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.WarehouseEntity;
import kz.offerprocessservice.service.CityService;
import kz.offerprocessservice.service.MerchantService;
import kz.offerprocessservice.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WarehouseProcessor {

    private final WarehouseService warehouseService;
    private final MerchantService merchantService;
    private final CityService cityService;

    public WarehouseEntity createWarehouse(String name, String merchantId, String cityId) throws CustomException {
        MerchantEntity merchantEntity = merchantService.findEntityById(merchantId);
        CityEntity cityEntity = cityService.findById(cityId);
        return warehouseService.saveOne(name, merchantEntity, cityEntity);
    }

    public WarehouseEntity getWarehouseById(String warehouseId) throws CustomException {
        return warehouseService.getOne(warehouseId);
    }

    public void deleteOne(String warehouseId) throws CustomException {
        warehouseService.deleteOne(warehouseId);
    }
}
