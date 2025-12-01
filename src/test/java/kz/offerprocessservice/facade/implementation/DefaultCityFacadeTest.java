package kz.offerprocessservice.facade.implementation;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.mapper.CityMapper;
import kz.offerprocessservice.model.dto.CityDTO;
import kz.offerprocessservice.model.entity.CityEntity;
import kz.offerprocessservice.service.CityService;
import kz.offerprocessservice.util.ErrorMessageSource;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultCityFacadeTest {

    private static final String ID = "ID";
    private static final String CITY_NAME = "cityName";

    @Spy
    private CityMapper mapper = Mappers.getMapper(CityMapper.class);

    @Mock
    private CityService service;

    @InjectMocks
    private DefaultCityFacade underTest;

    @Test
    void saveOne_throwsException_whenCityExists() throws CustomException {
        //given
        CityDTO cityDTO = new CityDTO();
        cityDTO.setName(CITY_NAME);
        when(service.saveOne(CITY_NAME)).thenThrow(CustomException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(ErrorMessageSource.CITY_ALREADY_EXISTS.getText(CITY_NAME))
                .build()
        );
        //when
        CustomException exception = assertThrows(CustomException.class, () -> underTest.saveOne(cityDTO));
        //then
        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(ErrorMessageSource.CITY_ALREADY_EXISTS.getText(CITY_NAME), exception.getMessage());
    }

    @Test
    void saveOne_returnsCity() throws CustomException {
        //given
        CityDTO cityDTO = new CityDTO();
        cityDTO.setName(CITY_NAME);
        CityEntity cityEntity = Instancio.create(CityEntity.class);
        cityEntity.setName(CITY_NAME);
        when(service.saveOne(cityDTO.getName())).thenReturn(cityEntity);
        //when
        CityDTO result = underTest.saveOne(cityDTO);
        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(cityEntity.getId());
        assertThat(result.getName()).isEqualTo(cityEntity.getName());
        assertThat(result.getCreatedAt()).isEqualTo(cityEntity.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(cityEntity.getUpdatedAt());
    }

    @Test
    void findOne_throwsException_whenCityNotFound() throws CustomException {
        //given
        when(service.findById(ID)).thenThrow(CustomException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(ErrorMessageSource.CITY_NOT_FOUND.getText(ID))
                .build()
        );
        //when
        CustomException exception = assertThrows(CustomException.class, () -> underTest.findOne(ID));
        //then
        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(ErrorMessageSource.CITY_NOT_FOUND.getText(ID), exception.getMessage());
    }

    @Test
    void findOne_returnsCity() throws CustomException {
        //given
        CityEntity cityEntity = Instancio.create(CityEntity.class);
        cityEntity.setId(ID);
        when(service.findById(ID)).thenReturn(cityEntity);
        //when
        CityDTO result = underTest.findOne(ID);
        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(cityEntity.getId());
        assertThat(result.getName()).isEqualTo(cityEntity.getName());
        assertThat(result.getCreatedAt()).isEqualTo(cityEntity.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(cityEntity.getUpdatedAt());
    }

    @Test
    void deleteOne_throwsException_whenCityNotFound() throws CustomException {
        //given
        doThrow(
                CustomException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message(ErrorMessageSource.CITY_NOT_FOUND.getText(ID))
                        .build()
        )
                .when(service)
                .deleteOne(ID);
        //when
        CustomException exception = assertThrows(CustomException.class, () -> underTest.deleteOne(ID));
        //then
        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(ErrorMessageSource.CITY_NOT_FOUND.getText(ID), exception.getMessage());
    }

    @Test
    void deleteOne_deletesCity() throws CustomException {
        //given
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        doNothing().when(service).deleteOne(anyString());
        //when
        underTest.deleteOne(ID);
        //then
        verify(service).deleteOne(captor.capture());
        String capturedId = captor.getValue();
        assertThat(ID).isEqualTo(capturedId);
    }

}