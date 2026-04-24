package com.saasauth.multitenant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saasauth.multitenant.dto.ApiResponse;
import com.saasauth.multitenant.dto.AuthResponse;
import com.saasauth.multitenant.dto.LoginRequest;
import com.saasauth.multitenant.dto.RegisterRequest;
import com.saasauth.multitenant.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

     private final AuthService authService;

     @PostMapping("/register")
     public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
          AuthResponse data = authService.register(request);
          return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Registration successful", data));
     }

     @PostMapping("/login")
     public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
          AuthResponse data = authService.login(request);
          return ResponseEntity.ok(ApiResponse.ok("Login successful", data));
     }
}