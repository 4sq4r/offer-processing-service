package kz.offerprocessservice.mapper;

import kz.offerprocessservice.model.dto.WarehouseDTO;
import kz.offerprocessservice.model.entity.WarehouseEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    WarehouseDTO toDTO(WarehouseEntity entity);

    WarehouseEntity toEntity(WarehouseDTO dto);
}
