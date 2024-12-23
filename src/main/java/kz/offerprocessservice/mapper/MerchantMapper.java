package kz.offerprocessservice.mapper;

import kz.offerprocessservice.model.dto.MerchantDTO;
import kz.offerprocessservice.model.entity.MerchantEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MerchantMapper {

    MerchantDTO toDTO(MerchantEntity entity);

    MerchantEntity toEntity(MerchantDTO dto);
}
