package kz.offerprocessservice.service;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.entity.CityEntity;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.WarehouseEntity;
import kz.offerprocessservice.repository.WarehouseRepository;
import kz.offerprocessservice.util.ErrorMessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarehouseService {

    private final WarehouseRepository repository;

    @Transactional(rollbackFor = CustomException.class)
    public WarehouseEntity saveOne(String name,
                                   MerchantEntity merchantEntity,
                                   CityEntity cityEntity
    ) throws CustomException {
        validateName(name, merchantEntity.getId());
        WarehouseEntity entity = new WarehouseEntity();
        entity.setName(name.trim());
        entity.setMerchant(merchantEntity);
        entity.setCity(cityEntity);
        entity.setName(name.trim());

        return repository.save(entity);
    }

    public WarehouseEntity getOne(String id) throws CustomException {
        return findEntityById(id);
    }

    public Set<WarehouseEntity> getAllWarehousesByMerchantId(String id) {
        return repository.findAllByMerchantId(id);
    }

    public Set<String> getAllWarehouseNamesByMerchantId(String id) {
        return repository.findAllNamesByMerchantId(id)
                .stream()
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Transactional(rollbackFor = CustomException.class)
    public void deleteOne(String id) throws CustomException {
        repository.delete(findEntityById(id));
    }


    private void validateName(String name, String merchantId) throws CustomException {
        if (repository.existsByNameIgnoreCaseAndMerchantId(name, merchantId)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(ErrorMessageSource.POINT_OF_SALE_ALREADY_EXISTS.getText(name))
                    .build();
        }

    }

    private WarehouseEntity findEntityById(String id) throws CustomException {
        return repository.findById(id).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message(ErrorMessageSource.POINT_OF_SALE_NOT_FOUND.getText(id))
                        .build()
        );
    }
}
