package kz.offerprocessservice.exception;

import kz.offerprocessservice.model.dto.ErrorResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static util.Fields.EMAIL;
import static util.Fields.NAME;

class GlobalExceptionHandlerTest {

    private static final String OBJECT_NAME = "dto";
    private static final String ERROR_MESSAGE = "Something went wrong";

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleMethodArgumentNotValid_returnsValidationErrorResponse() {
        //given
        FieldError fieldError1 = buildFieldError(NAME, ERROR_MESSAGE);
        FieldError fieldError2 = buildFieldError(EMAIL, null);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), OBJECT_NAME);
        bindingResult.addError(fieldError1);
        bindingResult.addError(fieldError2);
        MethodArgumentNotValidException methodArgumentNotValidException =
                new MethodArgumentNotValidException(null, bindingResult);
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        WebRequest request = mock(WebRequest.class);
        //when
        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                methodArgumentNotValidException, headers, BAD_REQUEST, request
        );
        // then
        assertEquals(BAD_REQUEST, response.getStatusCode());
        ErrorResponseDTO errorResponseDTO = (ErrorResponseDTO) response.getBody();
        assertNotNull(errorResponseDTO);
        assertEquals("Validation Error", errorResponseDTO.getMessage());
        assertEquals(BAD_REQUEST.value(), errorResponseDTO.getCode());
        assertNotNull(errorResponseDTO.getDateTime());
        Map<String, String> invalidFields = errorResponseDTO.getInvalidFields();
        assertNotNull(invalidFields);
        assertEquals(2, invalidFields.size());
        assertEquals(ERROR_MESSAGE, invalidFields.get(NAME));
        assertEquals("Invalid value", invalidFields.get(EMAIL));
    }

    @Test
    void handleCustomException_returnsCustomErrorResponse() {
        //given
        CustomException exception = new CustomException(NOT_FOUND, ERROR_MESSAGE);
        //when
        ResponseEntity<ErrorResponseDTO> response = handler.handleCustomException(exception);
        //then
        assertErrorResponse(
                NOT_FOUND,
                ERROR_MESSAGE,
                response
        );
    }

    @Test
    void handleException_returnsCustomErrorResponse() {
        //given
        Exception exception = new Exception(ERROR_MESSAGE);
        //when
        ResponseEntity<ErrorResponseDTO> response = handler.handleException(exception);
        //then
        assertErrorResponse(
                INTERNAL_SERVER_ERROR,
                "Internal server error: " + ERROR_MESSAGE,
                response
        );
    }

    private FieldError buildFieldError(String field, String message) {
        return new FieldError(OBJECT_NAME, field, message);
    }

    private void assertErrorResponse(
            HttpStatus expectedHttpStatus,
            String expectedMessage,
            ResponseEntity<ErrorResponseDTO> actual
    ) {
        assertNotNull(actual.getBody());
        assertEquals(expectedHttpStatus, actual.getStatusCode());
        assertEquals(expectedMessage, actual.getBody().getMessage());
        assertEquals(expectedHttpStatus.value(), actual.getBody().getCode());
    }
}