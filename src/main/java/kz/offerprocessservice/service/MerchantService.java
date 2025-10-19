package kz.offerprocessservice.service;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.mapper.MerchantMapper;
import kz.offerprocessservice.model.dto.MerchantDTO;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.repository.MerchantRepository;
import kz.offerprocessservice.util.ErrorMessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MerchantService {

    private final MerchantRepository repository;
    private final MerchantMapper mapper;

    @Transactional(rollbackFor = Exception.class)
    public MerchantDTO saveOne(MerchantDTO dto) throws CustomException {
        dto.setName(validateName(dto.getName()));
        MerchantEntity entity = mapper.toEntity(dto);
        repository.save(entity);

        return mapper.toDTO(entity);
    }

    public MerchantDTO getOne(String id) throws CustomException {
        return mapper.toDTO(findEntityById(id));
    }

    public void deleteOne(String id) throws CustomException {
        MerchantEntity entity = findEntityById(id);
        repository.delete(entity);
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

    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    public MerchantEntity findEntityById(String id) throws CustomException {
        return repository.findById(id).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message(ErrorMessageSource.MERCHANT_NOT_FOUND.getText(id))
                        .build()
        );
    }
}
