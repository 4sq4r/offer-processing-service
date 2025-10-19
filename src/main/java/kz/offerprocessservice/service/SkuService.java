package kz.offerprocessservice.service;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.mapper.SkuMapper;
import kz.offerprocessservice.model.dto.SkuDTO;
import kz.offerprocessservice.model.entity.SkuEntity;
import kz.offerprocessservice.repository.SkuRepository;
import kz.offerprocessservice.util.ErrorMessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkuService {

    private final SkuRepository repository;
    private final SkuMapper mapper;

    @Transactional(rollbackFor = Exception.class)
    public SkuDTO saveOne(SkuDTO dto) throws CustomException {
        dto.setName(validateName(dto.getName()));
        SkuEntity entity = mapper.toEntity(dto);
        repository.save(entity);

        return mapper.toDTO(entity);
    }

    public SkuDTO getOne(String id) throws CustomException {
        return mapper.toDTO(repository.findById(id).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message(ErrorMessageSource.SKU_NOT_FOUND.getText(id))
                        .build()));
    }

    private String validateName(String name) throws CustomException {
        name = name.trim();

        if (repository.existsByNameIgnoreCase(name)) {
            throw CustomException.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message(ErrorMessageSource.MERCHANT_ALREADY_EXISTS.getText(name))
                    .build();
        }

        return name;
    }
}
