package kz.offerprocessservice.service;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.entity.CityEntity;
import kz.offerprocessservice.repository.CityRepository;
import kz.offerprocessservice.util.ErrorMessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public CityEntity saveOne(String name) throws CustomException {
        validateCityName(name);
        String trimmedName = name.trim();
        isExists(trimmedName);
        CityEntity entity = new CityEntity();
        entity.setName(trimmedName);
        entity = repository.save(entity);

        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(String id) throws CustomException {
        repository.delete(findById(id));
    }

    private void validateCityName(String cityName) throws CustomException {
        if (cityName == null || cityName.trim().isEmpty()) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(ErrorMessageSource.CITY_NAME_IS_INVALID.getText(cityName))
                    .build();
        }

        if (cityName.matches(".*\\d.*")) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(ErrorMessageSource.CITY_NAME_IS_INVALID.getText(cityName))
                    .build();
        }
    }

    private void isExists(String name) throws CustomException {
        if (repository.existsByNameIgnoreCase(name)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(ErrorMessageSource.CITY_ALREADY_EXISTS.getText(name))
                    .build();
        }
    }

    public CityEntity findById(String id) throws CustomException {
        return repository.findById(id).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(HttpStatus.NOT_FOUND)
                        .message(ErrorMessageSource.CITY_NOT_FOUND.getText(id))
                        .build()
        );
    }
}
