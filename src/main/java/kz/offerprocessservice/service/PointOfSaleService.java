package kz.offerprocessservice.service;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.mapper.PointOfSaleMapper;
import kz.offerprocessservice.model.dto.PointOfSaleDTO;
import kz.offerprocessservice.model.entity.CityEntity;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.PointOfSaleEntity;
import kz.offerprocessservice.repository.PointOfSaleRepository;
import kz.offerprocessservice.util.ErrorMessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointOfSaleService {

    private final PointOfSaleRepository repository;
    private final PointOfSaleMapper mapper;
    private final MerchantService merchantService;
    private final CityService cityService;

    @Transactional(rollbackFor = Exception.class)
    public PointOfSaleDTO saveOne(PointOfSaleDTO dto) throws CustomException {
        MerchantEntity merchantEntity = merchantService.findEntityById(dto.getMerchantId());
        CityEntity cityEntity = cityService.findEntityById(dto.getCityId());
        dto.setName(validateName(dto.getName(), merchantEntity.getId()));
        PointOfSaleEntity entity = mapper.toEntity(dto);
        entity.setMerchant(merchantEntity);
        entity.setCity(cityEntity);
        repository.save(entity);

        return mapper.toDTO(entity);
    }

    public PointOfSaleDTO getOne(UUID id) throws CustomException {
        return mapper.toDTO(findEntityById(id));
    }

    public List<String> getAllPosNames(UUID id) {
        return repository.findAllByMerchantId(id).stream()
                .sorted()
                .toList();
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

    private PointOfSaleEntity findEntityById(UUID id) throws CustomException {
        return repository.findById(id).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message(ErrorMessageSource.POINT_OF_SALE_NOT_FOUND.getText(id.toString()))
                        .build()
        );
    }
}
