package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.file.FileStrategyProviderImpl;
import kz.offerprocessservice.file.validation.FileValidationStrategy;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListStatus;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.service.MinioService;
import kz.offerprocessservice.service.PriceListService;
import kz.offerprocessservice.service.WarehouseService;
import kz.offerprocessservice.service.rabbit.producer.PriceListValidationRabbitProducer;
import kz.offerprocessservice.service.statemachine.action.ActionNames;
import kz.offerprocessservice.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component(ActionNames.START_VALIDATION)
public class ValidationAction extends PriceListAction {

    private final PriceListValidationRabbitProducer priceListValidationRabbitProducer;
    private final MinioService minioService;
    private final WarehouseService warehouseService;
    private final FileStrategyProviderImpl fileStrategyProvider;

    public ValidationAction(
            PriceListService priceListService,
            PriceListValidationRabbitProducer priceListValidationRabbitProducer,
            MinioService minioService,
            WarehouseService warehouseService,
            FileStrategyProviderImpl fileStrategyProvider
    ) {
        super(priceListService);
        this.priceListValidationRabbitProducer = priceListValidationRabbitProducer;
        this.minioService = minioService;
        this.warehouseService = warehouseService;
        this.fileStrategyProvider = fileStrategyProvider;
    }

    @Override
    public void doExecute(String priceListId, StateContext<PriceListStatus, PriceListEvent> context) {
        PriceListEntity priceListEntity = updatePriceListStatus(priceListId, PriceListStatus.VALIDATION);
        boolean validated = validate(priceListEntity);
        priceListValidationRabbitProducer.sendValidationResult(priceListId, validated);
    }

    private boolean validate(PriceListEntity priceListEntity) throws CustomException {
        Set<String> warehouseNames = prepareWarehouseNames(priceListEntity);
        FileValidationStrategy validationStrategy = fileStrategyProvider.getValidationStrategy(
                priceListEntity.getFormat()
        );

        try (InputStream inputStream = minioService.getFile(priceListEntity.getUrl())) {
            return validationStrategy.validate(inputStream, warehouseNames);
        } catch (Exception e) {
            return false;
        }
    }

    private Set<String> prepareWarehouseNames(PriceListEntity priceListEntity) {
        Set<String> warehouseNames = new HashSet<>(warehouseService.getAllWarehouseNamesByMerchantId(
                priceListEntity.getMerchant().getId())
        );

        if (!priceListEntity.getFormat().equals(FileFormat.XML)) {
            warehouseNames.add(FileUtils.OFFER_CODE);
            warehouseNames.add(FileUtils.OFFER_NAME);
        }

        return warehouseNames;
    }
}
