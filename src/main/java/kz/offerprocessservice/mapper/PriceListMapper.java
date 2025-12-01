package kz.offerprocessservice.mapper;

import kz.offerprocessservice.model.dto.PriceListDTO;
import kz.offerprocessservice.model.entity.PriceListEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PriceListMapper {

    @Mapping(target = "merchant", ignore = true)
    PriceListEntity toEntity(PriceListDTO dto);

    @Mapping(source = "merchant.id", target = "merchantId")
    PriceListDTO toDTO(PriceListEntity entity);
}
