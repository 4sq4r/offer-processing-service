package kz.offerprocessservice.mapper;

import kz.offerprocessservice.model.dto.PriceListDTO;
import kz.offerprocessservice.model.entity.PriceListEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceListMapper {

    PriceListEntity toEntity(PriceListDTO dto);

    PriceListDTO toDTO(PriceListEntity entity);
}
