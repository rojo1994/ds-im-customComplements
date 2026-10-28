package com.coppel.exceptions.customcomplements;

import com.coppel.constants.customcomplements.AppMessages;
import com.coppel.constants.customcomplements.ComplementsMeta;
import com.coppel.dto.ApiResponseDTO;
import com.coppel.utils.LogUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

import static com.coppel.constants.Estado.FALLO;

/**
 * Errores del API: responden con el {@code ApiResponseDTO} común y el mensaje en {@code meta.message}. El detalle
 * técnico ({@code devMessage}) sólo se expone si {@code app.errors.include-dev-message=true}.
 */
@RestControllerAdvice(basePackages = "com.coppel.controller")
public class ComplementsExceptionHandler {

    private final LogUtil logUtil;
    private final boolean includeDevMessage;

    public ComplementsExceptionHandler(LogUtil logUtil,
                                       @Value("${app.errors.include-dev-message:false}") boolean includeDevMessage) {
        this.logUtil = logUtil;
        this.includeDevMessage = includeDevMessage;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleBusiness(BusinessException ex) {
        Throwable cause = ex.getCause();
        if (cause != null) {
            logUtil.grabar(FALLO, "ComplementsExceptionHandler.handleBusiness", ex.getMessage() + " | " + cause.getMessage());
        }
        ApiResponseDTO<Void> body = ApiResponseDTO.error(ex.getMeta().getMeta(ex.getStatus(), ex.getMessage(),
            includeDevMessage && cause != null ? cause.getMessage() : null));
        return ResponseEntity.status(HttpStatus.valueOf(ex.getMeta().getStatusCode())).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(ComplementsExceptionHandler::formatFieldError)
            .collect(Collectors.joining("; "));
        return build(ComplementsMeta.META_ERROR, message, null);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleMethodValidation(HandlerMethodValidationException ex) {
        String message = ex.getAllErrors().stream()
            .map(error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : error.toString())
            .distinct()
            .collect(Collectors.joining("; "));
        return build(ComplementsMeta.META_ERROR, message, null);
    }

    /** Validaciones de parámetros simples (@PathVariable / @RequestParam) con @Validated en el controlador. */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .distinct()
            .collect(Collectors.joining("; "));
        return build(ComplementsMeta.META_ERROR, message, null);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
        HttpMessageNotReadableException.class})
    public ResponseEntity<ApiResponseDTO<Void>> handleBadInput(Exception ex) {
        String message = ex instanceof MissingServletRequestParameterException missing
            ? String.format("Falta el parámetro requerido: %s", missing.getParameterName())
            : "La petición tiene un formato inválido.";
        return build(ComplementsMeta.META_ERROR, message, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleGeneral(Exception ex) {
        logUtil.grabar(FALLO, "ComplementsExceptionHandler.handleGeneral", ex.toString());
        return build(ComplementsMeta.META_ERROR_INTERNO, AppMessages.ERROR_INTERNO, ex.getMessage());
    }

    private ResponseEntity<ApiResponseDTO<Void>> build(ComplementsMeta meta, String message, String devMessage) {
        ApiResponseDTO<Void> body = ApiResponseDTO.error(meta.getMeta(message, includeDevMessage ? devMessage : null));
        return ResponseEntity.status(HttpStatus.valueOf(meta.getStatusCode())).body(body);
    }

    private static String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
