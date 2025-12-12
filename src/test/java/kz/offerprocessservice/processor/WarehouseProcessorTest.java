package kz.offerprocessservice.processor;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.entity.CityEntity;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.WarehouseEntity;
import kz.offerprocessservice.service.CityService;
import kz.offerprocessservice.service.MerchantService;
import kz.offerprocessservice.service.WarehouseService;
import kz.offerprocessservice.util.ErrorMessageSource;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.Data.CITY_ID;
import static util.Data.MERCHANT_ID;
import static util.Data.NAME;
import static util.Data.WAREHOUSE_ID;

@ExtendWith(MockitoExtension.class)
class WarehouseProcessorTest {

    @Mock
    private WarehouseService warehouseService;

    @Mock
    private MerchantService merchantService;

    @Mock
    private CityService cityService;

    @InjectMocks
    private WarehouseProcessor underTest;

    @Test
    void createWarehouse_throwsException_whenMerchantNotFound() {
        //given
        when(merchantService.findEntityById(MERCHANT_ID)).thenThrow(
                new CustomException(HttpStatus.NOT_FOUND, ErrorMessageSource.MERCHANT_NOT_FOUND.getText(MERCHANT_ID)));
        //when
        CustomException exception = assertThrows(CustomException.class,
                () -> underTest.createWarehouse(NAME, MERCHANT_ID, CITY_ID));
        //then
        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals(ErrorMessageSource.MERCHANT_NOT_FOUND.getText(MERCHANT_ID), exception.getMessage());
    }

    @Test
    void createWarehouse_throwsException_whenCityNotFound() {
        //given
        when(merchantService.findEntityById(MERCHANT_ID)).thenReturn(new MerchantEntity());
        when(cityService.findById(CITY_ID)).thenThrow(
                new CustomException(HttpStatus.NOT_FOUND, ErrorMessageSource.CITY_NOT_FOUND.getText(CITY_ID)));
        //when
        CustomException exception = assertThrows(CustomException.class,
                () -> underTest.createWarehouse(NAME, MERCHANT_ID, CITY_ID));
        //then
        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals(ErrorMessageSource.CITY_NOT_FOUND.getText(CITY_ID), exception.getMessage());
    }

    @Test
    void createWarehouse_createsWarehouse() {
        //given
        MerchantEntity merchantEntity = Instancio.create(MerchantEntity.class);
        CityEntity cityEntity = Instancio.create(CityEntity.class);
        WarehouseEntity warehouseEntity = Instancio.create(WarehouseEntity.class);
        warehouseEntity.setName(NAME);
        warehouseEntity.setMerchant(merchantEntity);
        warehouseEntity.setCity(cityEntity);
        when(merchantService.findEntityById(MERCHANT_ID)).thenReturn(merchantEntity);
        when(cityService.findById(CITY_ID)).thenReturn(cityEntity);
        when(warehouseService.saveOne(NAME, merchantEntity, cityEntity)).thenReturn(warehouseEntity);
        //when
        WarehouseEntity result = underTest.createWarehouse(NAME, MERCHANT_ID, CITY_ID);
        //then
        assertNotNull(result);
        assertThat(result).isEqualTo(warehouseEntity);
    }

    @Test
    void getWarehouseById_throwsException_whenWarehouseNotFound() {
        //given
        when(warehouseService.getOne(WAREHOUSE_ID)).thenThrow(
                new CustomException(HttpStatus.NOT_FOUND, ErrorMessageSource.POINT_OF_SALE_NOT_FOUND.getText(WAREHOUSE_ID))
        );
        //when
        CustomException exception = assertThrows(CustomException.class, () -> underTest.getWarehouseById(WAREHOUSE_ID));
        //then
        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals(ErrorMessageSource.POINT_OF_SALE_NOT_FOUND.getText(WAREHOUSE_ID), exception.getMessage());
    }

    @Test
    void getWarehouseById_returnsWarehouse() {
        //given
        WarehouseEntity warehouseEntity = Instancio.create(WarehouseEntity.class);
        warehouseEntity.setId(WAREHOUSE_ID);
        when(warehouseService.getOne(WAREHOUSE_ID)).thenReturn(warehouseEntity);
        //when
        WarehouseEntity result = warehouseService.getOne(WAREHOUSE_ID);
        //then
        assertThat(result).isEqualTo(result);
    }

    @Test
    void deleteOne_throwsException_whenWarehouseNotFound() {
        //given
        doThrow(new CustomException(
                HttpStatus.NOT_FOUND,
                ErrorMessageSource.POINT_OF_SALE_NOT_FOUND.getText(WAREHOUSE_ID)
        )).when(warehouseService).deleteOne(WAREHOUSE_ID);
        //when
        CustomException exception = assertThrows(CustomException.class, () -> underTest.deleteOne(WAREHOUSE_ID));
        //then
        assertNotNull(exception);
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals(ErrorMessageSource.POINT_OF_SALE_NOT_FOUND.getText(WAREHOUSE_ID), exception.getMessage());
    }

    @Test
    void deleteOne_deletesWarehouse() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        //when
        underTest.deleteOne(WAREHOUSE_ID);
        //then
        verify(warehouseService).deleteOne(captor.capture());
        String id = captor.getValue();
        assertThat(id).isEqualTo(WAREHOUSE_ID);
    }

}