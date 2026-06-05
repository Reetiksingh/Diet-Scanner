package com.nutrilens.nutrition.api;

import com.nutrilens.common.domain.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<ApiError.FieldViolation> details = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiError.FieldViolation(error.getField(), error.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(new ApiError(Instant.now(), request.getRequestURI(), 400,
                "VALIDATION_FAILED", "Request validation failed.", details, request.getHeader("X-Request-Id")));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> general(Exception exception, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(Instant.now(), request.getRequestURI(), 400,
                "NUTRITION_REQUEST_FAILED", exception.getMessage(), List.of(), request.getHeader("X-Request-Id")));
    }
}

