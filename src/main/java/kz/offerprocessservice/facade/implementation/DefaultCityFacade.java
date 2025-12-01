package kz.offerprocessservice.facade.implementation;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.facade.CityFacade;
import kz.offerprocessservice.mapper.CityMapper;
import kz.offerprocessservice.model.dto.CityDTO;
import kz.offerprocessservice.model.entity.CityEntity;
import kz.offerprocessservice.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultCityFacade implements CityFacade {

    private final CityMapper mapper;
    private final CityService service;

    @Override
    public CityDTO saveOne(CityDTO dto) throws CustomException {
        CityEntity cityEntity = service.saveOne(dto.getName());
        return mapper.toDTO(cityEntity);
    }

    @Override
    public CityDTO findOne(String id) throws CustomException {
        return mapper.toDTO(service.findById(id));
    }

    @Override
    public void deleteOne(String id) throws CustomException {
        service.deleteOne(id);
    }
}
