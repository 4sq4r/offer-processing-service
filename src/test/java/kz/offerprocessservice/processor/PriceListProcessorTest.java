package kz.offerprocessservice.processor;

import jakarta.xml.bind.JAXBException;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.file.FileStrategyProvider;
import kz.offerprocessservice.file.templating.FileTemplatingStrategy;
import kz.offerprocessservice.model.dto.minio.MinioMetaData;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.service.MerchantService;
import kz.offerprocessservice.service.MinioService;
import kz.offerprocessservice.service.PriceListService;
import kz.offerprocessservice.service.WarehouseService;
import kz.offerprocessservice.service.rabbit.producer.PriceListValidationRabbitProducer;
import kz.offerprocessservice.util.ErrorMessageSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Data.FILE_NAME;
import static util.Data.FILE_URL;
import static util.Data.MERCHANT_ID;
import static util.Data.PRICE_LIST_ID;
import static util.Data.WAREHOUSE_1_ID;
import static util.Data.WAREHOUSE_2_ID;

@ExtendWith(MockitoExtension.class)
class PriceListProcessorTest {


    @Mock
    private MinioService minioService;

    @Mock
    private PriceListService priceListService;

    @Mock
    private MerchantService merchantService;

    @Mock
    private WarehouseService warehouseService;

    @Mock
    private FileStrategyProvider fileStrategyProvider;

    @Mock
    private PriceListValidationRabbitProducer priceListValidationRabbitProducer;

    @Mock
    private FileTemplatingStrategy templatingStrategy;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private PriceListProcessor underTest;

    @Test
    void uploadPriceList_throwsException_whenMerchantNotFound() throws CustomException {
        //given
        when(merchantService.findEntityById(MERCHANT_ID))
                .thenThrow(new CustomException(HttpStatus.BAD_REQUEST, ErrorMessageSource.MERCHANT_NOT_FOUND.getText(MERCHANT_ID)));
        //when
        CustomException exception = assertThrows(CustomException.class, () -> underTest.uploadPriceList(MERCHANT_ID, multipartFile));
        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getMessage()).isEqualTo(ErrorMessageSource.MERCHANT_NOT_FOUND.getText(MERCHANT_ID));
        verify(minioService, never()).uploadFile(any());
        verify(priceListService, never()).savePriceList(any(), any(), any(), any(), any());
        verify(priceListValidationRabbitProducer, never()).sendToValidation(any());
    }

    @Test
    void uploadPriceList_success() {
        // given
        MerchantEntity merchant = new MerchantEntity();
        merchant.setId(MERCHANT_ID);

        MinioMetaData meta = MinioMetaData.builder().fileName(FILE_NAME).url(FILE_URL).format(FileFormat.EXCEL.name()).build();

        PriceListEntity saved = new PriceListEntity();
        saved.setId(PRICE_LIST_ID);

        when(merchantService.findEntityById(MERCHANT_ID)).thenReturn(merchant);
        when(minioService.uploadFile(multipartFile)).thenReturn(meta);
        when(priceListService.savePriceList(eq(merchant), eq(multipartFile), eq(FILE_NAME), eq(FILE_URL), eq(FileFormat.EXCEL.name()))).thenReturn(saved);

        // when
        PriceListEntity result = underTest.uploadPriceList(MERCHANT_ID, multipartFile);

        // then
        assertNotNull(result);
        assertEquals(PRICE_LIST_ID, result.getId());

        verify(priceListValidationRabbitProducer).sendToValidation(PRICE_LIST_ID);
    }

    @Test
    void downloadTemplate_success() throws Exception {
        // given
        Set<String> warehouseNames = Set.of(WAREHOUSE_1_ID, WAREHOUSE_2_ID);
        when(warehouseService.getAllWarehouseNamesByMerchantId(MERCHANT_ID)).thenReturn(warehouseNames);
        when(fileStrategyProvider.getTemplatingStrategy(FileFormat.EXCEL)).thenReturn(templatingStrategy);
        ResponseEntity<byte[]> response = ResponseEntity.ok("test".getBytes());
        when(templatingStrategy.generate(anySet())).thenReturn(response);
        // when
        ResponseEntity<byte[]> result = underTest.downloadTemplate(MERCHANT_ID, FileFormat.EXCEL);

        // then
        assertNotNull(result);
        assertArrayEquals("test".getBytes(), result.getBody());

        ArgumentCaptor<Set<String>> captor = ArgumentCaptor.forClass(Set.class);
        verify(templatingStrategy).generate(captor.capture());

        Set<String> captured = captor.getValue();
        assertEquals(2, captured.size());
        assertTrue(captured.contains(WAREHOUSE_1_ID));
        assertTrue(captured.contains(WAREHOUSE_2_ID));
    }

    @Test
    void downloadTemplate_throwsJaxbException() throws Exception {
        // given
        when(warehouseService.getAllWarehouseNamesByMerchantId(MERCHANT_ID)).thenReturn(Set.of(WAREHOUSE_1_ID));
        when(fileStrategyProvider.getTemplatingStrategy(FileFormat.EXCEL)).thenReturn(templatingStrategy);
        when(templatingStrategy.generate(anySet())).thenThrow(new JAXBException("XML generation failed"));
        // when / then
        assertThrows(JAXBException.class, () -> underTest.downloadTemplate(MERCHANT_ID, FileFormat.EXCEL));
    }
}