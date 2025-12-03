package kz.offerprocessservice.facade;

import kz.offerprocessservice.mapper.WarehouseMapper;
import kz.offerprocessservice.model.dto.WarehouseDTO;
import kz.offerprocessservice.model.entity.WarehouseEntity;
import kz.offerprocessservice.processor.WarehouseProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WarehouseFacade {

    private final WarehouseMapper mapper;
    private final WarehouseProcessor processor;

    public WarehouseDTO saveOne(WarehouseDTO warehouseDTO) {
        WarehouseEntity entity = processor.createWarehouse(
                warehouseDTO.getName(),
                warehouseDTO.getMerchantId(),
                warehouseDTO.getCityId()
        );

        return mapper.toDTO(entity);
    }

    public WarehouseDTO getOne(String warehouseId) {
        return mapper.toDTO(processor.getWarehouseById(warehouseId));
    }

    public void deleteOne(String warehouseId) {
        processor.deleteOne(warehouseId);
    }
}
