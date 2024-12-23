package kz.offerprocessservice.mapper;

import kz.offerprocessservice.model.dto.CityDTO;
import kz.offerprocessservice.model.entity.CityEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CityMapper {

    CityDTO toDTO(CityEntity entity);

    CityEntity toEntity(CityDTO dto);
}
