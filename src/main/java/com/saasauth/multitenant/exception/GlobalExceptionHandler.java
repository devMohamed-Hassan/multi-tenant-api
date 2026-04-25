package com.saasauth.multitenant.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.saasauth.multitenant.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

     @ExceptionHandler(MethodArgumentNotValidException.class)
     public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
               MethodArgumentNotValidException ex) {

          Map<String, String> errors = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                              FieldError::getField,
                              FieldError::getDefaultMessage));

          return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(new ApiResponse<>(false, "Validation failed", errors));
     }

     @ExceptionHandler(UserAlreadyExistsException.class)
     public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
          return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(ex.getMessage()));
     }

     @ExceptionHandler(AuthenticationException.class)
     public ResponseEntity<ApiResponse<Void>> handleAuthException(AuthenticationException ex) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Access denied: missing or invalid token"));
     }

     @ExceptionHandler(AccessDeniedException.class)
     public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
          return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied: insufficient permissions"));
     }

     @ExceptionHandler(RuntimeException.class)
     public ResponseEntity<ApiResponse<Void>> handleRuntime(RuntimeException ex) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ex.getMessage()));
     }
}