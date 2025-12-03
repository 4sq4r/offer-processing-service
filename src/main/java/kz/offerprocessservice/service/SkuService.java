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
    public SkuDTO saveOne(SkuDTO dto) {
        dto.setName(validateName(dto.getName()));
        SkuEntity entity = mapper.toEntity(dto);
        repository.save(entity);

        return mapper.toDTO(entity);
    }

    public SkuDTO getOne(String id) {
        return mapper.toDTO(repository.findById(id).orElseThrow(
                () -> new CustomException(HttpStatus.BAD_REQUEST, ErrorMessageSource.SKU_NOT_FOUND.getText(id)))
        );
    }

    private String validateName(String name) {
        name = name.trim();

        if (repository.existsByNameIgnoreCase(name)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorMessageSource.MERCHANT_ALREADY_EXISTS.getText(name));
        }

        return name;
    }
}
