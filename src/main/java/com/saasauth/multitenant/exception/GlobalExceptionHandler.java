package com.saasauth.multitenant.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

     @ExceptionHandler(RuntimeException.class)
     public ResponseEntity<ApiResponse<Void>> handleRuntime(RuntimeException ex) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(ex.getMessage()));
     }
}