package kz.offerprocessservice.service;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.entity.CityEntity;
import kz.offerprocessservice.repository.CityRepository;
import kz.offerprocessservice.util.ErrorMessageSource;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    private static final String ID = "ID";
    private static final String CITY_NAME = "ALMATY";

    @Mock
    CityRepository repository;

    @InjectMocks
    private CityService underTest;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(repository);
    }

    @ParameterizedTest
    @MethodSource("argumentsFor_saveOne_throwsException_whenInvalidCityName")
    void saveOne_throwsException_whenInvalidCityName(String name) {
        CustomException e = assertThrows(CustomException.class, () -> underTest.saveOne(name));

        assertNotNull(e);
        assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        assertEquals(ErrorMessageSource.CITY_NAME_IS_INVALID.getText(name), e.getMessage());
    }

    private static Stream<String> argumentsFor_saveOne_throwsException_whenInvalidCityName() {
        return Stream.of(
                null,
                "",
                "123",
                "asd123"
        );
    }

    @Test
    void saveOne_throwsException_whenCityAlreadyExists() {
        //given
        when(repository.existsByNameIgnoreCase(CITY_NAME)).thenReturn(true);
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.saveOne(CITY_NAME));
        assertNotNull(e);
        assertEquals(ErrorMessageSource.CITY_ALREADY_EXISTS.getText(CITY_NAME), e.getMessage());
    }

    @ParameterizedTest
    @MethodSource("argumentsFor_saveOne_savesCity")
    void saveOne_savesCity(String cityName) throws CustomException {
        //given
        ArgumentCaptor<CityEntity> captor = ArgumentCaptor.forClass(CityEntity.class);
        String trimmed = cityName.trim();
        CityEntity cityEntity = Instancio.create(CityEntity.class);
        cityEntity.setName(trimmed);
        when(repository.existsByNameIgnoreCase(trimmed)).thenReturn(false);
        when(repository.save(any())).thenReturn(cityEntity);
        //when
        CityEntity result = underTest.saveOne(cityName);
        //then
        verify(repository).save(captor.capture());
        CityEntity saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo(trimmed);

        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(cityEntity.getId());
        assertThat(result.getName()).isEqualTo(cityEntity.getName());
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    private static Stream<String> argumentsFor_saveOne_savesCity() {
        return Stream.of(
                "Almaty             ",
                "           Almaty"
        );
    }

    @Test
    void findById_throwsException_whenCityNotFound() {
        //given
        when(repository.findById(ID)).thenReturn(Optional.empty());
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.findById(ID));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        assertEquals(ErrorMessageSource.CITY_NOT_FOUND.getText(ID), e.getMessage());
    }

    @Test
    void findById_returnsCity() throws CustomException {
        //given
        CityEntity cityEntity = Instancio.create(CityEntity.class);
        cityEntity.setId(ID);
        when(repository.findById(ID)).thenReturn(Optional.of(cityEntity));
        //when
        CityEntity result = underTest.findById(ID);
        //then
        assertNotNull(result);
        assertThat(result.getId()).isEqualTo(ID);
        assertThat(result.getName()).isEqualTo(cityEntity.getName());
        assertThat(result.getCreatedAt()).isEqualTo(cityEntity.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(cityEntity.getUpdatedAt());
    }

    @Test
    void deleteOne_throwsException_whenCityNotFound() {
        //given
        when(repository.findById(ID)).thenReturn(Optional.empty());
        //when
        CustomException e = assertThrows(CustomException.class, () -> underTest.deleteOne(ID));
        //then
        assertNotNull(e);
        assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        assertEquals(ErrorMessageSource.CITY_NOT_FOUND.getText(ID), e.getMessage());
    }

    @Test
    void deleteOne_deletesCity() throws CustomException {
        //given
        CityEntity cityEntity = Instancio.create(CityEntity.class);
        cityEntity.setId(ID);
        when(repository.findById(ID)).thenReturn(Optional.of(cityEntity));
        //when
        underTest.deleteOne(ID);
        //then
        verify(repository).delete(cityEntity);
    }
}