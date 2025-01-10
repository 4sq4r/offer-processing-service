package kz.offerprocessservice.service;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.mapper.CityMapper;
import kz.offerprocessservice.model.dto.CityDTO;
import kz.offerprocessservice.repository.CityRepository;
import kz.offerprocessservice.util.ErrorMessageSource;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock
    private CityRepository cityRepository;
    @Spy
    private CityMapper cityMapper;
    @InjectMocks
    private CityService underTest;

    @Test
    public void saveOne_throwsException_whenNameAlreadyExists() {
        CityDTO dto = Instancio.create(CityDTO.class);
        when(cityRepository.existsByNameIgnoreCase(dto.getName())).thenReturn(true);
        CustomException exception = assertThrows(CustomException.class, () -> underTest.saveOne(dto));
        //when
        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(ErrorMessageSource.CITY_ALREADY_EXISTS.getText(dto.getName()), exception.getMessage());
    }
}