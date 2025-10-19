package kz.offerprocessservice.processor;

import jakarta.xml.bind.JAXBException;
import kz.offerprocessservice.configuration.MinioProperties;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.file.FileStrategyProviderImpl;
import kz.offerprocessservice.file.templating.FileTemplatingStrategy;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.service.MerchantService;
import kz.offerprocessservice.service.MinioService;
import kz.offerprocessservice.service.PriceListService;
import kz.offerprocessservice.service.WarehouseService;
import kz.offerprocessservice.service.rabbit.producer.PriceListValidationRabbitProducer;
import kz.offerprocessservice.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceListProcessor {

    private final MinioProperties minioProperties;
    private final MinioService minioService;
    private final PriceListService priceListService;
    private final MerchantService merchantService;
    private final PriceListValidationRabbitProducer priceListValidationRabbitProducer;
    private final WarehouseService warehouseService;
    private final FileStrategyProviderImpl fileStrategyProvider;

    @Transactional(rollbackFor = CustomException.class)
    public PriceListEntity uploadPriceList(String merchantId, MultipartFile file) throws CustomException {
        MerchantEntity merchantEntity = merchantService.findEntityById(merchantId);
        String[] split = file.getOriginalFilename().split("\\.");
        String format = split[split.length - 1];
        String salt = UUID.randomUUID().toString();
        String fileName = salt + "." + format;
        String url = StringUtils.MINIO_FILE_FORMAT.formatted(minioProperties.getPriceListsFolder(), fileName);
        minioService.uploadFile(file, StringUtils.MINIO_FILE_FORMAT.formatted(minioProperties.getPriceListsUrl(), fileName));

        PriceListEntity priceListEntity = priceListService.savePriceList(merchantEntity, file, fileName, url, format);
        priceListValidationRabbitProducer.sendToValidation(priceListEntity.getId());

        return priceListEntity;
    }

    public ResponseEntity<byte[]> downloadTemplate(String merchantId, FileFormat format) throws JAXBException, IOException {
        Set<String> warehouseNames = warehouseService.getAllWarehouseNamesByMerchantId(merchantId);
        FileTemplatingStrategy strategy = fileStrategyProvider.getTemplatingStrategy(format);
        Set<String> extendedNames = new LinkedHashSet<>(warehouseNames);

        return strategy.generate(extendedNames);
    }
}
