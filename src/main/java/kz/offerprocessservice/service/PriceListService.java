package kz.offerprocessservice.service;

import jakarta.xml.bind.JAXBException;
import kz.offerprocessservice.event.FileUploadedEvent;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.mapper.PriceListMapper;
import kz.offerprocessservice.model.dto.PriceListDTO;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.model.enums.PriceListStatus;
import kz.offerprocessservice.repository.PriceListRepository;
import kz.offerprocessservice.strategy.file.FileStrategyProviderImpl;
import kz.offerprocessservice.strategy.file.templating.FileTemplatingStrategy;
import kz.offerprocessservice.util.ErrorMessageSource;
import kz.offerprocessservice.util.FileUtils;
import kz.offerprocessservice.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceListService {

    @Value("${minio.price-lists-folder}")
    private String priceListFolderName;
    @Value("${minio.price-lists-url}")
    private String actualMinioUrl;

    private final PriceListRepository priceListRepository;
    private final MinioService minioService;
    private final PriceListMapper priceListMapper;
    private final WarehouseService warehouseService;
    private final ApplicationEventPublisher eventPublisher;
    private final FileStrategyProviderImpl fileStrategyProvider;

    @Transactional(rollbackFor = Exception.class)
    public PriceListDTO uploadPriceList(UUID merchantId, MultipartFile file) throws CustomException {
        String[] split = file.getOriginalFilename().split("\\.");
        String salt = UUID.randomUUID().toString();
        String fileName = salt + "." + split[split.length - 1];
        String url = StringUtils.MINIO_FILE_FORMAT.formatted(priceListFolderName, fileName);
        minioService.uploadFile(file, StringUtils.MINIO_FILE_FORMAT.formatted(actualMinioUrl, fileName));
        PriceListEntity entity = new PriceListEntity();
        entity.setName(fileName);
        entity.setMerchantId(merchantId);
        entity.setOriginalName(file.getOriginalFilename());
        entity.setUrl(url);
        entity.setStatus(PriceListStatus.NEW);
        entity.setFormat(FileFormat.fromExtension("." + split[split.length - 1]));
        priceListRepository.save(entity);
        eventPublisher.publishEvent(new FileUploadedEvent(this, entity.getId()));

        return priceListMapper.toDTO(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<byte[]> downloadTemplate(UUID merchantId, FileFormat format) throws IOException, JAXBException {
        Set<String> warehouseNames = warehouseService.getAllWarehouseNamesByMerchantId(merchantId);
        FileTemplatingStrategy strategy = fileStrategyProvider.getTemplatingStrategy(format);
        Set<String> extendedNames = new LinkedHashSet<>();
        extendedNames.add(FileUtils.OFFER_CODE);
        extendedNames.add(FileUtils.OFFER_NAME);
        extendedNames.addAll(warehouseNames);

        return strategy.generate(extendedNames);
    }

    @Transactional(rollbackFor = Exception.class)
    public PriceListEntity findEntityById(UUID id) throws CustomException {
        return priceListRepository.findById(id).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message(ErrorMessageSource.PRICE_LIST_NOT_FOUND.getText(id.toString()))
                        .build()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public PriceListEntity updateStatus(PriceListEntity entity) {
        return priceListRepository.save(entity);
    }
}
