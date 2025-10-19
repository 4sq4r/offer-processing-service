package kz.offerprocessservice.service.statemachine.action;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.file.FileStrategyProviderImpl;
import kz.offerprocessservice.file.validation.FileValidationStrategy;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListState;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.service.MinioService;
import kz.offerprocessservice.service.PriceListService;
import kz.offerprocessservice.service.WarehouseService;
import kz.offerprocessservice.service.rabbit.producer.PriceListValidationRabbitProducer;
import kz.offerprocessservice.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component(ActionNames.START_VALIDATION)
@RequiredArgsConstructor
public class ValidationAction implements PriceListAction {

    private final PriceListValidationRabbitProducer priceListValidationRabbitProducer;
    private final MinioService minioService;
    private final PriceListService priceListService;
    private final WarehouseService warehouseService;
    private final FileStrategyProviderImpl fileStrategyProvider;

    @Override
    public void doExecute(UUID priceListId, StateContext<PriceListState, PriceListEvent> context) throws CustomException {

        try {
            log.info("Handling file upload event: {}", priceListId);
            PriceListEntity ple = priceListService.findEntityById(priceListId);
            ple.setStatus(PriceListState.VALIDATION);
            priceListService.updateState(ple.getId(), PriceListState.VALIDATION);
            boolean validated = validate(ple);

            priceListValidationRabbitProducer.sendValidationResult(priceListId, validated);
            log.info("Validation price list for {} ended with result: {}", priceListId, validated);
        } catch (CustomException e) {
            log.error("Error processing file upload event: {}", e.getMessage());
        }
    }

    private boolean validate(PriceListEntity priceListEntity) throws CustomException {
        Set<String> warehouseNames = warehouseService.getAllWarehouseNamesByMerchantId(priceListEntity.getMerchant().getId());

        if (!priceListEntity.getFormat().equals(FileFormat.XML)) {
            warehouseNames.add(FileUtils.OFFER_CODE);
            warehouseNames.add(FileUtils.OFFER_NAME);
        }

        FileValidationStrategy validationStrategy = fileStrategyProvider.getValidationStrategy(priceListEntity.getFormat());
        PriceListState status;
        String failReason = null;

        try (InputStream inputStream = minioService.getFile(priceListEntity.getUrl())) {
            if (validationStrategy.validate(inputStream, warehouseNames)) {
                status = PriceListState.VALIDATED;
            } else {
                failReason = "Incorrect warehouse names.";
                status = PriceListState.VALIDATION_FAILED;
            }
        } catch (Exception e) {
            failReason = "Unable to validate file: " + e.getMessage();
            status = PriceListState.VALIDATION_FAILED;
        }

        return status == PriceListState.VALIDATED;
    }
}
