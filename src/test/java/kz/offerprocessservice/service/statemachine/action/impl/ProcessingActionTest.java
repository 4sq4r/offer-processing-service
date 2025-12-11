package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.file.FileStrategyProviderImpl;
import kz.offerprocessservice.file.processing.impl.XmlProcessingStrategyImpl;
import kz.offerprocessservice.model.PriceListStatus;
import kz.offerprocessservice.model.dto.PriceListItemDTO;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.OfferEntity;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.processor.OfferProcessor;
import kz.offerprocessservice.processor.StockProcessor;
import kz.offerprocessservice.service.MerchantService;
import kz.offerprocessservice.service.MinioService;
import kz.offerprocessservice.service.statemachine.action.AbstractPriceListActionTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import static kz.offerprocessservice.configuration.PriceListStateMachineConfiguration.PRICE_LIST_ID_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ProcessingActionTest extends AbstractPriceListActionTest<ProcessingAction> {

    @Mock
    private StockProcessor stockProcessor;

    @Mock
    private OfferProcessor offerProcessor;

    @Mock
    private MerchantService merchantService;

    @Mock
    private FileStrategyProviderImpl fileStrategyProvider;

    @Mock
    private XmlProcessingStrategyImpl xmlProcessingStrategy;

    @Mock
    private MinioService minioService;

    @Override
    protected ProcessingAction createAction() {
        return new ProcessingAction(
                priceListService,
                stockProcessor,
                merchantService,
                fileStrategyProvider,
                minioService,
                offerProcessor
        );
    }

    @Test
    void doExecute_marksProcessingFailed_withException() throws SAXException {
        // given
        PriceListEntity priceList = new PriceListEntity();
        priceList.setId("pl1");
        priceList.setUrl("file.xml");
        priceList.setFormat(FileFormat.XML);
        MerchantEntity merchant = new MerchantEntity();
        merchant.setId("m1");
        priceList.setMerchant(merchant);

        when(context.getMessageHeader(PRICE_LIST_ID_HEADER)).thenReturn("pl1");
        when(priceListService.findEntityById("pl1")).thenReturn(priceList);
        when(minioService.getFile(any())).thenReturn(new ByteArrayInputStream("<items></items>".getBytes()));
        when(merchantService.findEntityById("m1")).thenReturn(merchant);
        when(fileStrategyProvider.getProcessingStrategy(FileFormat.XML)).thenReturn(xmlProcessingStrategy);
        when(xmlProcessingStrategy.extract(any(InputStream.class))).thenThrow(new SAXException("fail"));
        when(priceListService.updateOne(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        action.execute(context);

        // then
        assertEquals(PriceListStatus.PROCESSING_FAILED, priceList.getStatus());
        assertEquals("Failed to parse file: fail", priceList.getFailReason());
        verifyNoInteractions(offerProcessor, stockProcessor);
    }

    @Test
    void doExecute_marksProcessingFailed_withEmptyPriceListItems() throws SAXException {
        // given
        PriceListEntity priceList = new PriceListEntity();
        priceList.setId("pl1");
        priceList.setUrl("file.xml");
        priceList.setFormat(FileFormat.XML);
        MerchantEntity merchant = new MerchantEntity();
        merchant.setId("m1");
        priceList.setMerchant(merchant);

        when(context.getMessageHeader(PRICE_LIST_ID_HEADER)).thenReturn("pl1");
        when(priceListService.findEntityById("pl1")).thenReturn(priceList);
        when(minioService.getFile(any())).thenReturn(new ByteArrayInputStream("<items></items>".getBytes()));
        when(merchantService.findEntityById("m1")).thenReturn(merchant);
        when(fileStrategyProvider.getProcessingStrategy(FileFormat.XML)).thenReturn(xmlProcessingStrategy);
        when(xmlProcessingStrategy.extract(any(InputStream.class))).thenReturn(Set.of());
        when(priceListService.updateOne(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        action.execute(context);

        // then
        assertEquals(PriceListStatus.PROCESSING_FAILED, priceList.getStatus());
        assertEquals("Processing failed. Empty price list items.", priceList.getFailReason());
        verifyNoInteractions(offerProcessor, stockProcessor);
    }

    @Test
    void doExecute_returns_withEmptyOffers() throws Exception {
        // given
        PriceListEntity priceList = new PriceListEntity();
        priceList.setId("pl2");
        priceList.setUrl("file.xml");
        priceList.setFormat(FileFormat.XML);
        MerchantEntity merchant = new MerchantEntity();
        merchant.setId("m1");
        priceList.setMerchant(merchant);

        PriceListItemDTO item = new PriceListItemDTO();
        item.setOfferCode("O1");
        item.setOfferName("Offer1");
        item.setStocks(Map.of());

        when(context.getMessageHeader(PRICE_LIST_ID_HEADER)).thenReturn("pl2");
        when(priceListService.findEntityById("pl2")).thenReturn(priceList);
        when(minioService.getFile(any())).thenReturn(new ByteArrayInputStream("<items></items>".getBytes()));
        when(merchantService.findEntityById("m1")).thenReturn(merchant);
        when(fileStrategyProvider.getProcessingStrategy(FileFormat.XML)).thenReturn(xmlProcessingStrategy);
        when(xmlProcessingStrategy.extract(any(InputStream.class))).thenReturn(Set.of(item));
        when(offerProcessor.saveOffers(Set.of(item), merchant)).thenReturn(Set.of());
        when(priceListService.updateOne(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        action.execute(context);

        // then
        assertEquals(PriceListStatus.PROCESSED, priceList.getStatus());
        verifyNoInteractions(stockProcessor);
        verify(offerProcessor).saveOffers(Set.of(item), merchant);
    }

    @Test
    void doExecute_processedPriceListItems() throws Exception {
        // given
        PriceListEntity priceList = new PriceListEntity();
        priceList.setId("pl2");
        priceList.setUrl("file.xml");
        priceList.setFormat(FileFormat.XML);
        MerchantEntity merchant = new MerchantEntity();
        merchant.setId("m1");
        priceList.setMerchant(merchant);

        PriceListItemDTO item = new PriceListItemDTO();
        item.setOfferCode("O1");
        item.setOfferName("Offer1");
        item.setStocks(Map.of());

        when(context.getMessageHeader(PRICE_LIST_ID_HEADER)).thenReturn("pl2");
        when(priceListService.findEntityById("pl2")).thenReturn(priceList);
        when(minioService.getFile(any())).thenReturn(new ByteArrayInputStream("<items></items>".getBytes()));
        when(merchantService.findEntityById("m1")).thenReturn(merchant);
        when(fileStrategyProvider.getProcessingStrategy(FileFormat.XML)).thenReturn(xmlProcessingStrategy);
        when(xmlProcessingStrategy.extract(any(InputStream.class))).thenReturn(Set.of(item));
        when(offerProcessor.saveOffers(Set.of(item), merchant)).thenReturn(Set.of(new OfferEntity()));
        when(priceListService.updateOne(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        action.execute(context);

        // then
        assertEquals(PriceListStatus.PROCESSED, priceList.getStatus());
        verify(offerProcessor).saveOffers(Set.of(item), merchant);
    }

}