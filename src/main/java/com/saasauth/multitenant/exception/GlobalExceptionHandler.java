package com.saasauth.multitenant.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.saasauth.multitenant.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

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