//package kz.offerprocessservice.service.statemachine.action.impl;
//
//import kz.offerprocessservice.file.FileStrategyProviderImpl;
//import kz.offerprocessservice.model.entity.MerchantEntity;
//import kz.offerprocessservice.model.entity.PriceListEntity;
//import kz.offerprocessservice.model.enums.FileFormat;
//import kz.offerprocessservice.service.MinioService;
//import kz.offerprocessservice.service.WarehouseService;
//import kz.offerprocessservice.service.rabbit.producer.PriceListValidationRabbitProducer;
//import kz.offerprocessservice.service.statemachine.action.AbstractPriceListActionTest;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.io.ByteArrayInputStream;
//import java.util.Set;
//
//import static kz.offerprocessservice.configuration.PriceListStateMachineConfiguration.PRICE_LIST_ID_HEADER;
//import static org.mockito.Mockito.when;
//import static util.Data.PRICE_LIST_ID;
//
//@ExtendWith(MockitoExtension.class)
//class ValidationActionTest extends AbstractPriceListActionTest<ValidationAction> {
//
//    @Mock
//    private PriceListValidationRabbitProducer priceListValidationRabbitProducer;
//
//    @Mock
//    private MinioService minioService;
//
//    @Mock
//    private WarehouseService warehouseService;
//
//    @Mock
//    private FileStrategyProviderImpl fileStrategyProvider;
//
//    @Override
//    protected ValidationAction createAction() {
//        return new ValidationAction(
//                priceListService,
//                priceListValidationRabbitProducer,
//                minioService,
//                warehouseService,
//                fileStrategyProvider
//        );
//    }
//
//    @Test
//    void execute_validated_updatesStatus() {
//        //given
//        PriceListEntity priceListEntity = buildPriceList();
//
//        when(context.getMessageHeader(PRICE_LIST_ID_HEADER)).thenReturn(PRICE_LIST_ID);
//        when(priceListService.findEntityById(PRICE_LIST_ID)).thenReturn(priceListEntity);
//        when(warehouseService.getAllWarehouseNamesByMerchantId("m1"))
//                .thenReturn(Set.of("W1", "W2"));
//
//        when(minioService.getFile(priceListEntity.getUrl()))
//                .thenReturn(new ByteArrayInputStream("<xml></xml>".getBytes()));
//
//        when(fileStrategyProvider.getValidationStrategy(FileFormat.XML))
//                .thenReturn(validationStrategy);
//
//        when(validationStrategy.validate(any(), any()))
//                .thenReturn(true);
//
//        // when
//        action.execute(context);
//
//        // then
//        assertEquals(PriceListStatus.VALIDATED, entity.getStatus());
//
//        verify(priceListService, atLeastOnce()).updateOne(entity);
//        verify(priceListValidationRabbitProducer)
//                .sendValidationResult(PRICE_LIST_ID, true);
//    }
//
//    private PriceListEntity buildPriceList() {
//        PriceListEntity priceListEntity = new PriceListEntity();
//        priceListEntity.setId(PRICE_LIST_ID);
//        priceListEntity.setUrl("file.xml");
//        priceListEntity.setFormat(FileFormat.XML);
//
//        MerchantEntity merchant = new MerchantEntity();
//        merchant.setId("m1");
//        priceListEntity.setMerchant(merchant);
//
//        return priceListEntity;
//    }
//
//}
