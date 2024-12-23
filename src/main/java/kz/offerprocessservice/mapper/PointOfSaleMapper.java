package kz.offerprocessservice.mapper;

import kz.offerprocessservice.model.dto.PointOfSaleDTO;
import kz.offerprocessservice.model.entity.PointOfSaleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PointOfSaleMapper {

    PointOfSaleDTO toDTO(PointOfSaleEntity entity);

    PointOfSaleEntity toEntity(PointOfSaleDTO dto);
}
