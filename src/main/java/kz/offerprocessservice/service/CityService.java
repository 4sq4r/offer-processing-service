package kz.offerprocessservice.service;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.mapper.CityMapper;
import kz.offerprocessservice.model.dto.CityDTO;
import kz.offerprocessservice.model.entity.CityEntity;
import kz.offerprocessservice.repository.CityRepository;
import kz.offerprocessservice.util.ErrorMessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    @Transactional(rollbackFor = Exception.class)
    public CityDTO saveOne(CityDTO dto) throws CustomException {
        dto.setName(validateName(dto.getName()));
        CityEntity entity = cityMapper.toEntity(dto);
        cityRepository.save(entity);

        return cityMapper.toDTO(entity);
    }

    public CityDTO getOne(UUID id) throws CustomException {
        return cityMapper.toDTO(findEntityById(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(UUID id) throws CustomException {
        cityRepository.delete(findEntityById(id));
    }

    private String validateName(String name) throws CustomException {
        name = name.trim();

        if (cityRepository.existsByNameIgnoreCase(name)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(ErrorMessageSource.CITY_ALREADY_EXISTS.getText(name))
                    .build();
        }

        return name;
    }

    private CityEntity findEntityById(UUID id) throws CustomException {
        return cityRepository.findById(id).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message(ErrorMessageSource.CITY_NOT_FOUND.getText(id.toString()))
                        .build()
        );
    }
}
