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
        String trimmedName = name.trim();
        validateName(trimmedName);
        CityEntity entity = new CityEntity();
        entity.setName(trimmedName);

        return repository.save(entity);
    }

    public CityEntity getOne(String id) throws CustomException {
        return findEntityById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(String id) throws CustomException {
        repository.delete(findEntityById(id));
    }

    private void validateName(String name) throws CustomException {
        if (repository.existsByNameIgnoreCase(name)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(ErrorMessageSource.CITY_ALREADY_EXISTS.getText(name))
                    .build();
        }
    }

    public CityEntity findEntityById(String id) throws CustomException {
        return repository.findById(id).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message(ErrorMessageSource.CITY_NOT_FOUND.getText(id.toString()))
                        .build()
        );
    }
}
