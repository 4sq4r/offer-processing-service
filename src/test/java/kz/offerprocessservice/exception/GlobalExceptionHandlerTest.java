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


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleMethodArgumentNotValid_returnsValidationErrorResponse() {
        FieldError fieldError = new FieldError(
                "dto", "name", "Name must not be blank"
        );

        MethodArgumentNotValidException methodArgumentNotValidException =
                new MethodArgumentNotValidException(
                        null,
                        new BeanPropertyBindingResult(new Object(), "dto") {{
                            addError(fieldError);
                        }}
                );

        HttpHeaders headers = new HttpHeaders();
        WebRequest request = mock(WebRequest.class);

        ResponseEntity<Object> response =
                handler.handleMethodArgumentNotValid(methodArgumentNotValidException, headers, HttpStatus.BAD_REQUEST, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponseDTO errorResponseDTO = (ErrorResponseDTO) response.getBody();
        assertNotNull(errorResponseDTO);
        assertEquals("Validation Error", errorResponseDTO.getMessage());
        assertEquals(1, errorResponseDTO.getInvalidFields().size());
        assertEquals("Name must not be blank", errorResponseDTO.getInvalidFields().get("name"));
    }

    @Test
    void handleCustomException_returnsCustomErrorResponse() {
        CustomException exception = CustomException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message("Something bad")
                .build();

        ResponseEntity<ErrorResponseDTO> response = handler.handleCustomException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Something bad", response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getCode());
    }
}