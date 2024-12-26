package kz.offerprocessservice.service;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.mapper.WarehouseMapper;
import kz.offerprocessservice.model.dto.WarehouseDTO;
import kz.offerprocessservice.model.entity.CityEntity;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.WarehouseEntity;
import kz.offerprocessservice.repository.WarehouseRepository;
import kz.offerprocessservice.util.ErrorMessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseService {

    private final WarehouseRepository repository;
    private final WarehouseMapper mapper;
    private final MerchantService merchantService;
    private final CityService cityService;

    @Transactional(rollbackFor = Exception.class)
    public WarehouseDTO saveOne(WarehouseDTO dto) throws CustomException {
        MerchantEntity merchantEntity = merchantService.findEntityById(dto.getMerchantId());
        CityEntity cityEntity = cityService.findEntityById(dto.getCityId());
        dto.setName(validateName(dto.getName(), merchantEntity.getId()));
        WarehouseEntity entity = mapper.toEntity(dto);
        entity.setMerchant(merchantEntity);
        entity.setCity(cityEntity);
        repository.save(entity);

        return mapper.toDTO(entity);
    }

    public WarehouseDTO getOne(UUID id) throws CustomException {
        return mapper.toDTO(findEntityById(id));
    }

    public Set<WarehouseEntity> getAllWarehousesByMerchantId(UUID id) {
        if (merchantService.existsById(id)) {
            return repository.findAllByMerchantId(id);
        }

        return new HashSet<>();
    }

    public Set<String> getAllWarehouseNamesByMerchantId(UUID id) {
        return repository.findAllNamesByMerchantId(id)
                .stream()
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(UUID id) throws CustomException {
        repository.delete(findEntityById(id));
    }


    private String validateName(String name, UUID merchantId) throws CustomException {
        name = name.trim();

        if (repository.existsByNameIgnoreCaseAndMerchantId(name, merchantId)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(ErrorMessageSource.POINT_OF_SALE_ALREADY_EXISTS.getText(name))
                    .build();
        }

        return name;
    }

    private WarehouseEntity findEntityById(UUID id) throws CustomException {
        return repository.findById(id).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message(ErrorMessageSource.POINT_OF_SALE_NOT_FOUND.getText(id.toString()))
                        .build()
        );
    }
}
