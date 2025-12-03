package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.file.FileStrategyProviderImpl;
import kz.offerprocessservice.file.processing.impl.XmlProcessingStrategyImpl;
import kz.offerprocessservice.model.PriceListStatus;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.service.MerchantService;
import kz.offerprocessservice.service.MinioService;
import kz.offerprocessservice.service.OfferService;
import kz.offerprocessservice.service.StockService;
import kz.offerprocessservice.service.WarehouseService;
import kz.offerprocessservice.service.statemachine.action.AbstractPriceListActionTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;

import static kz.offerprocessservice.configuration.PriceListStateMachineConfiguration.PRICE_LIST_ID_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Data.PRICE_LIST_ID;

class ProcessingActionTest extends AbstractPriceListActionTest<ProcessingAction> {

    @Mock
    private OfferService offerService;

    @Mock
    private StockService stockService;

    @Mock
    private MerchantService merchantService;

    @Mock
    private WarehouseService warehouseService;

    @Mock
    private FileStrategyProviderImpl fileStrategyProvider;

    @Mock
    private XmlProcessingStrategyImpl xmlProcessingStrategy;

    @Mock
    private MinioService minioService;

    @Override
    protected ProcessingAction createAction() {
        return new ProcessingAction(
                offerService,
                stockService,
                merchantService,
                priceListService,
                warehouseService,
                fileStrategyProvider,
                minioService
        );
    }

    @Test
    void doExecute_fileParsingThrowsSAXException_updatesProcessingFailed() throws Exception {
        PriceListEntity priceListEntity = new PriceListEntity();
        priceListEntity.setId(PRICE_LIST_ID);
        priceListEntity.setUrl("file.xml");
        priceListEntity.setFormat(FileFormat.XML);
        MerchantEntity merchant = new MerchantEntity();
        merchant.setId("m1");
        priceListEntity.setMerchant(merchant);

        when(context.getMessageHeader(PRICE_LIST_ID_HEADER)).thenReturn(PRICE_LIST_ID);
        when(priceListService.findEntityById(PRICE_LIST_ID)).thenReturn(priceListEntity);
        when(minioService.getFile(any())).thenReturn(new ByteArrayInputStream("<items></items>".getBytes()));
        when(merchantService.findEntityById("m1")).thenReturn(merchant);
        when(fileStrategyProvider.getProcessingStrategy(FileFormat.XML)).thenReturn(xmlProcessingStrategy);
        when(xmlProcessingStrategy.extract(any())).thenThrow(new SAXException("parse fail"));

        action.execute(context);

        verify(priceListService, atLeastOnce()).updateOne(priceListEntity);
        assertEquals(PriceListStatus.PROCESSING_FAILED, priceListEntity.getStatus());
        assertTrue(priceListEntity.getFailReason().contains("parse fail"));
    }

}