package kz.offerprocessservice.service;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.entity.CityEntity;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.WarehouseEntity;
import kz.offerprocessservice.repository.WarehouseRepository;
import kz.offerprocessservice.util.ErrorMessageSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository repository;

    public WarehouseEntity saveOne(
            String name,
            MerchantEntity merchantEntity,
            CityEntity cityEntity
    ) {
        validateName(name, merchantEntity.getId());
        WarehouseEntity entity = new WarehouseEntity();
        entity.setName(name.trim());
        entity.setMerchant(merchantEntity);
        entity.setCity(cityEntity);

        return repository.save(entity);
    }

    public WarehouseEntity getOne(String id) {
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
    public void deleteOne(String id) {
        repository.delete(findEntityById(id));
    }


    private void validateName(String name, String merchantId) {
        if (repository.existsByNameIgnoreCaseAndMerchantId(name, merchantId)) {
            throw new CustomException(
                    HttpStatus.BAD_REQUEST,
                    ErrorMessageSource.POINT_OF_SALE_ALREADY_EXISTS.getText(name)
            );
        }
    }

    private WarehouseEntity findEntityById(String id) {
        return repository.findById(id).orElseThrow(
                () -> new CustomException(
                        HttpStatus.NOT_FOUND,
                        ErrorMessageSource.POINT_OF_SALE_ALREADY_EXISTS.getText(id)
                )
        );
    }
}
