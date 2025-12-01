package kz.offerprocessservice.mapper;

import kz.offerprocessservice.model.dto.WarehouseDTO;
import kz.offerprocessservice.model.entity.WarehouseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    @Mapping(source = "merchant.id", target = "merchantId")
    @Mapping(source = "city.id", target = "cityId")
    WarehouseDTO toDTO(WarehouseEntity entity);

    WarehouseEntity toEntity(WarehouseDTO dto);
}
