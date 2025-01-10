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

    private final CityRepository repository;
    private final CityMapper mapper;

    @Transactional(rollbackFor = Exception.class)
    public CityDTO saveOne(CityDTO dto) throws CustomException {
//        dto.setName(validateName(dto.getName())); asd
        CityEntity entity = mapper.toEntity(dto);
        repository.save(entity);

        return mapper.toDTO(entity);
    }

    public CityDTO getOne(UUID id) throws CustomException {
        return mapper.toDTO(findEntityById(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(UUID id) throws CustomException {
        repository.delete(findEntityById(id));
    }

    private String validateName(String name) throws CustomException {
        name = name.trim();

        if (repository.existsByNameIgnoreCase(name)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(ErrorMessageSource.CITY_ALREADY_EXISTS.getText(name))
                    .build();
        }

        return name;
    }

    public CityEntity findEntityById(UUID id) throws CustomException {
        return repository.findById(id).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message(ErrorMessageSource.CITY_NOT_FOUND.getText(id.toString()))
                        .build()
        );
    }
}
