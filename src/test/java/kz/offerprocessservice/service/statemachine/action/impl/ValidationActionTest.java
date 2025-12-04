package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.file.FileStrategyProviderImpl;
import kz.offerprocessservice.file.validation.FileValidationStrategy;
import kz.offerprocessservice.model.PriceListStatus;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.service.MinioService;
import kz.offerprocessservice.service.WarehouseService;
import kz.offerprocessservice.service.rabbit.producer.PriceListValidationRabbitProducer;
import kz.offerprocessservice.service.statemachine.action.AbstractPriceListActionTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

import static kz.offerprocessservice.configuration.PriceListStateMachineConfiguration.PRICE_LIST_ID_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Data.PRICE_LIST_ID;

@ExtendWith(MockitoExtension.class)
class ValidationActionTest extends AbstractPriceListActionTest<ValidationAction> {

    private static final String MERCHANT_ID = "m1";
    private static final Set<String> WAREHOUSE_IDS = Set.of("wh1", "wh2");

    @Mock
    private PriceListValidationRabbitProducer priceListValidationRabbitProducer;

    @Mock
    private MinioService minioService;

    @Mock
    private WarehouseService warehouseService;

    @Mock
    private FileStrategyProviderImpl fileStrategyProvider;

    @Mock
    private FileValidationStrategy fileValidationStrategy;

    @Override
    protected ValidationAction createAction() {
        return new ValidationAction(
                priceListService,
                priceListValidationRabbitProducer,
                minioService,
                warehouseService,
                fileStrategyProvider
        );
    }

    @ParameterizedTest
    @MethodSource("argumentsFor_execute_validated_updatesStatus")
    void execute_validated_updatesStatus_updatesStatus(
            FileFormat format,
            boolean isValid,
            PriceListStatus expectedStatus
    ) throws IOException {
        // given
        PriceListEntity entity = buildPriceList(format);
        when(context.getMessageHeader(PRICE_LIST_ID_HEADER)).thenReturn(PRICE_LIST_ID);
        when(priceListService.findEntityById(PRICE_LIST_ID)).thenReturn(entity);
        when(priceListService.updateOne(entity)).thenReturn(entity);
        when(warehouseService.getAllWarehouseNamesByMerchantId(MERCHANT_ID))
                .thenReturn(WAREHOUSE_IDS);
        when(minioService.getFile(entity.getUrl()))
                .thenReturn(new ByteArrayInputStream("<file></file>".getBytes()));
        when(fileStrategyProvider.getValidationStrategy(format))
                .thenReturn(fileValidationStrategy);
        when(fileValidationStrategy.validate(any(), any()))
                .thenReturn(isValid);
        // when
        action.execute(context);

        // then
        assertEquals(expectedStatus, entity.getStatus());
        verify(priceListService, atLeastOnce()).updateOne(entity);
        verify(priceListValidationRabbitProducer)
                .sendValidationResult(PRICE_LIST_ID, isValid);

        if (!isValid) {
            assertEquals("Incorrect warehouse names.", entity.getFailReason());
        }
    }

    private static Stream<Arguments> argumentsFor_execute_validated_updatesStatus() {
        return Stream.of(
                Arguments.of(FileFormat.CSV, true, PriceListStatus.VALIDATED),
                Arguments.of(FileFormat.CSV, false, PriceListStatus.VALIDATION_FAILED),
                Arguments.of(FileFormat.XML, true, PriceListStatus.VALIDATED),
                Arguments.of(FileFormat.XML, false, PriceListStatus.VALIDATION_FAILED),
                Arguments.of(FileFormat.EXCEL, true, PriceListStatus.VALIDATED),
                Arguments.of(FileFormat.EXCEL, false, PriceListStatus.VALIDATION_FAILED)
        );
    }

    @ParameterizedTest
    @EnumSource(FileFormat.class)
    void execute_validation_throwsException_updatesValidationFailed(FileFormat format) {
        // given
        PriceListEntity entity = buildPriceList(format);

        when(context.getMessageHeader(PRICE_LIST_ID_HEADER)).thenReturn(PRICE_LIST_ID);
        when(priceListService.findEntityById(PRICE_LIST_ID)).thenReturn(entity);
        when(priceListService.updateOne(entity)).thenReturn(entity);
        when(warehouseService.getAllWarehouseNamesByMerchantId(MERCHANT_ID))
                .thenReturn(WAREHOUSE_IDS);

        when(minioService.getFile(entity.getUrl()))
                .thenThrow(new RuntimeException("file not found"));

        when(fileStrategyProvider.getValidationStrategy(format))
                .thenReturn(fileValidationStrategy);
        // when
        action.execute(context);
        // then
        assertEquals(PriceListStatus.VALIDATION_FAILED, entity.getStatus());
        assertTrue(entity.getFailReason().contains("Unable to validate file: file not found"));
        verify(priceListService, atLeastOnce()).updateOne(entity);
        verify(priceListValidationRabbitProducer)
                .sendValidationResult(PRICE_LIST_ID, false);
    }

    private PriceListEntity buildPriceList(FileFormat format) {
        PriceListEntity priceListEntity = new PriceListEntity();
        priceListEntity.setId(PRICE_LIST_ID);
        priceListEntity.setUrl("file." + format.name().toLowerCase());
        priceListEntity.setFormat(format);

        MerchantEntity merchant = new MerchantEntity();
        merchant.setId(MERCHANT_ID);
        priceListEntity.setMerchant(merchant);

        return priceListEntity;
    }

}
