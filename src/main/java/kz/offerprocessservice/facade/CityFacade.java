package kz.offerprocessservice.facade;

import jakarta.validation.constraints.NotNull;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.dto.CityDTO;

public interface CityFacade {

    CityDTO saveOne(CityDTO dto) throws CustomException;

    CityDTO findOne(@NotNull String id) throws CustomException;

    void deleteOne(@NotNull String id) throws CustomException;
}
