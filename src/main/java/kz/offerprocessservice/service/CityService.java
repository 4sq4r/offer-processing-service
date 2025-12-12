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
    public CityEntity saveOne(String name) {
        validateCityName(name);
        String trimmedName = name.trim();
        isExists(trimmedName);
        CityEntity entity = new CityEntity();
        entity.setName(trimmedName);
        entity = repository.save(entity);

        return entity;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(String id) {
        repository.delete(findById(id));
    }


    public CityEntity findById(String id) {
        return repository.findById(id).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, ErrorMessageSource.CITY_NOT_FOUND.getText(id))
        );
    }

    private void validateCityName(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorMessageSource.CITY_NAME_IS_INVALID.getText(cityName));
        }

        if (cityName.matches(".*\\d.*")) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorMessageSource.CITY_NAME_IS_INVALID.getText(cityName));
        }
    }

    private void isExists(String name) {
        if (repository.existsByNameIgnoreCase(name)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorMessageSource.CITY_ALREADY_EXISTS.getText(name));
        }
    }
}
