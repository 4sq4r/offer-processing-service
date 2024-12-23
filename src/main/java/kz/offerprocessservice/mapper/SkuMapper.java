package kz.offerprocessservice.mapper;

import kz.offerprocessservice.model.dto.SkuDTO;
import kz.offerprocessservice.model.entity.SkuEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SkuMapper {

    SkuDTO toDTO(SkuEntity entity);

    SkuEntity toEntity(SkuDTO dto);
}
