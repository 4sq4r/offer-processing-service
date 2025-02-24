package kz.offerprocessservice.event.handler;

import kz.offerprocessservice.event.FileUploadedEvent;
import kz.offerprocessservice.event.FileValidatedEvent;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.file.FileStrategyProviderImpl;
import kz.offerprocessservice.file.validation.FileValidationStrategy;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.model.enums.PriceListStatus;
import kz.offerprocessservice.processor.FileUploadProcessor;
import kz.offerprocessservice.service.MinioService;
import kz.offerprocessservice.service.PriceListService;
import kz.offerprocessservice.service.WarehouseService;
import kz.offerprocessservice.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.InputStream;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileUploadedEventHandler {

    private final ApplicationEventPublisher publisher;
    private final FileUploadProcessor processor;
    private final MinioService minioService;
    private final PriceListService priceListService;
    private final WarehouseService warehouseService;
    private final FileStrategyProviderImpl fileStrategyProvider;

    @Value("${minio.prefix-to-delete}")
    private String minioPrefixToDelete;

    @TransactionalEventListener
    public void handle(FileUploadedEvent event) {
        processor.processFileAsync(() -> {
            try {
                log.info("Handling file upload event: {}", event.getPriceListId());
                PriceListEntity ple = priceListService.findEntityById(event.getPriceListId());
                ple.setStatus(PriceListStatus.ON_VALIDATION);
                priceListService.updateStatus(ple);
                boolean validated = validate(ple);

                if (validated) {
                    publisher.publishEvent(new FileValidatedEvent(this, ple));
                }
            } catch (CustomException e) {
                log.error("Error processing file upload event: {}", e.getMessage());
            }
        });
    }

    private boolean validate(PriceListEntity priceListEntity) throws CustomException {
        Set<String> warehouseNames = warehouseService.getAllWarehouseNamesByMerchantId(priceListEntity.getMerchantId());
        FileValidationStrategy validationStrategy = fileStrategyProvider.getValidationStrategy(priceListEntity.getFormat());

        if (!priceListEntity.getFormat().equals(FileFormat.XML)) {
            warehouseNames.add(FileUtils.OFFER_CODE);
            warehouseNames.add(FileUtils.OFFER_NAME);
        }

        String failReason = null;
        PriceListStatus status;

        try (InputStream inputStream = minioService.getFile(priceListEntity.getUrl().replaceFirst(minioPrefixToDelete, ""))) {
            if (validationStrategy.validate(inputStream, warehouseNames)) {
                status = PriceListStatus.VALIDATED;
            } else {
                failReason = "Incorrect warehouse names.";
                status = PriceListStatus.VALIDATION_FAILED;
            }
        } catch (Exception e) {
            failReason = "Unable to validate file: " + e.getMessage();
            status = PriceListStatus.VALIDATION_FAILED;
        }

        priceListEntity.setStatus(status);
        priceListEntity.setFailReason(failReason);
        priceListService.updateStatus(priceListEntity);

        return status == PriceListStatus.VALIDATED;
    }
}
