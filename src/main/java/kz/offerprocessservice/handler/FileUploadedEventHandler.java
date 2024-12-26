package kz.offerprocessservice.handler;

import kz.offerprocessservice.event.FileUploadedEvent;
import kz.offerprocessservice.event.FileValidatedEvent;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.entity.PriceListEntity;
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

    private boolean validate(PriceListEntity priceList) throws CustomException {
        Set<String> pos = warehouseService.getAllWarehouseNamesByMerchantId(priceList.getMerchantId());
        String failReason = null;
        PriceListStatus status;

        try (InputStream is = minioService.getFile(priceList.getUrl().replaceFirst(minioPrefixToDelete, ""));) {
            if (FileUtils.validatePriceList(is, pos)) {
                status = PriceListStatus.VALIDATED;
            } else {
                failReason = "Incorrect headers.";
                status = PriceListStatus.VALIDATION_FAILED;
            }
        } catch (Exception e) {
            failReason = "Unable to validate file: " + e.getMessage();
            status = PriceListStatus.VALIDATION_FAILED;
        }

        priceList.setStatus(status);
        priceList.setFailReason(failReason);
        priceListService.updateStatus(priceList);

        return status == PriceListStatus.VALIDATED;
    }
}
