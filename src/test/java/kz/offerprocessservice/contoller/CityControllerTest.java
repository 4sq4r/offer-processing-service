package kz.offerprocessservice.contoller;

import kz.offerprocessservice.model.dto.CityDTO;
import kz.offerprocessservice.model.dto.ErrorResponseDTO;
import kz.offerprocessservice.model.entity.CityEntity;
import kz.offerprocessservice.repository.CityRepository;
import kz.offerprocessservice.repository.WarehouseRepository;
import kz.offerprocessservice.util.ErrorMessageSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.Fields.NAME;

class CityControllerTest extends AbstractControllerTest {

    private static final String URL_BASE = "/cities/v1";
    private static final String ID = "id";
    private static final String CITY_NAME = "cityName";

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @AfterEach
    void afterEach() {
        cityRepository.deleteAll();
        warehouseRepository.deleteAll();
    }

    @Test
    void saveOne_returnsBadRequest_whenNameIsBlank() {
        //when
        MvcResult mvcResult = sendPostRequest(URL_BASE, new CityDTO(), status().isBadRequest());
        //then
        ErrorResponseDTO errorResponse = readMvcResultAsString(mvcResult, ErrorResponseDTO.class);
        Map<String, String> invalidFields = errorResponse.getInvalidFields();

        assertEquals(1, invalidFields.size());
        assertTrue(invalidFields.containsKey(NAME));
        assertEquals("City name must be not null.", invalidFields.get(NAME));
    }

    @Test
    void saveOne_throwsException_whenCityNameIsExists() {
        //given
        saveCityEntity();
        //when
        MvcResult mvcResult = sendPostRequest(URL_BASE, buildCityDTO(), status().isBadRequest());
        //then
        ErrorResponseDTO errorResponse = readMvcResultAsString(mvcResult, ErrorResponseDTO.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getCode());
        assertEquals(ErrorMessageSource.CITY_ALREADY_EXISTS.getText(CITY_NAME), errorResponse.getMessage());
        assertNotNull(errorResponse.getDateTime());
    }

    @Test
    void saveOne_savesCity() {
        //given
        CityDTO dto = buildCityDTO();
        //when
        MvcResult mvcResult = sendPostRequest(URL_BASE, dto, status().is2xxSuccessful());
        //then
        CityDTO savedCityDTO = readMvcResultAsString(mvcResult, CityDTO.class);
        assertNotNull(savedCityDTO);
        assertEquals(dto.getName(), savedCityDTO.getName());
        assertSystemValuesIsNotNull(mvcResult);
    }

    @Test
    void getOne_throwsException_whenCityNotFound() {
        //when
        MvcResult mvcResult = sendGetRequest(URL_BASE + "/" + ID, status().isNotFound());
        //then
        ErrorResponseDTO errorResponse = readMvcResultAsString(mvcResult, ErrorResponseDTO.class);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getCode());
        assertEquals(ErrorMessageSource.CITY_NOT_FOUND.getText(ID), errorResponse.getMessage());
        assertNotNull(errorResponse.getDateTime());
    }

    @Test
    void getOne_returnsCity() {
        //given
        CityEntity cityEntity = saveCityEntity();
        String id = cityEntity.getId();
        //when
        MvcResult mvcResult = sendGetRequest(URL_BASE + "/" + id, status().isOk());
        //then
        CityDTO result = readMvcResultAsString(mvcResult, CityDTO.class);
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertSystemValuesIsNotNull(mvcResult);
    }

    @Test
    void deleteOne_throwsException_whenCityNotFound() {
        //when
        MvcResult mvcResult = sendDeleteRequest(URL_BASE + "/" + ID, status().isNotFound());
        //then
        ErrorResponseDTO errorResponse = readMvcResultAsString(mvcResult, ErrorResponseDTO.class);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getCode());
        assertEquals(ErrorMessageSource.CITY_NOT_FOUND.getText(ID), errorResponse.getMessage());
        assertNotNull(errorResponse.getDateTime());
    }

    @Test
    void deleteOne_deletesCity() {
        //given
        CityEntity cityEntity = saveCityEntity();
        String id = cityEntity.getId();
        //when
        sendDeleteRequest(URL_BASE + "/" + id, status().isNoContent());
        //then
        assertTrue(cityRepository.findById(id).isEmpty());
    }

    private CityDTO buildCityDTO() {
        CityDTO dto = new CityDTO();
        dto.setName(CITY_NAME);
        return dto;
    }

    private CityEntity saveCityEntity() {
        CityEntity entity = new CityEntity();
        entity.setName(CITY_NAME);
        return cityRepository.save(entity);
    }
}