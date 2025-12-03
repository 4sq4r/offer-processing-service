package kz.offerprocessservice.exception;

import kz.offerprocessservice.model.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            @NotNull HttpHeaders headers,
            HttpStatusCode status,
            @NotNull WebRequest request
    ) {
        Map<String, String> invalidFields = e.getFieldErrors().stream()
                .collect(
                        toMap(
                                FieldError::getField,
                                fe -> Optional.ofNullable(fe.getDefaultMessage()).orElse("Invalid value"),
                                (existing, replacement) -> replacement
                        )
                );
        ErrorResponseDTO errorResponseDTO = buildErrorResponse(
                status.value(),
                "Validation Error",
                invalidFields
        );

        return new ResponseEntity<>(errorResponseDTO, status);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDTO> handleCustomException(CustomException e) {
        ErrorResponseDTO errorResponseDTO = buildErrorResponse(
                e.getHttpStatus().value(),
                e.getMessage(),
                null
        );

        return new ResponseEntity<>(errorResponseDTO, e.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception e) {
        ErrorResponseDTO errorResponseDTO = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error: " + e.getMessage(),
                null
        );

        log.error("Unhandled exception", e);

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponseDTO buildErrorResponse(
            int code,
            String message,
            Map<String, String> invalidFields
    ) {
        return ErrorResponseDTO.builder()
                .dateTime(LocalDateTime.now())
                .code(code)
                .message(message)
                .invalidFields(invalidFields)
                .build();
    }
}
