package com.saasauth.multitenant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saasauth.multitenant.dto.ApiResponse;
import com.saasauth.multitenant.dto.AuthResponse;
import com.saasauth.multitenant.dto.LoginRequest;
import com.saasauth.multitenant.dto.RegisterRequest;
import com.saasauth.multitenant.model.RefreshToken;
import com.saasauth.multitenant.model.User;
import com.saasauth.multitenant.security.JwtUtil;
import com.saasauth.multitenant.service.AuthService;
import com.saasauth.multitenant.service.RefreshTokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

     private final AuthService authService;
     private final RefreshTokenService refreshTokenService;
     private final JwtUtil jwtUtil;

     @PostMapping("/register")
     public ResponseEntity<ApiResponse<AuthResponse>> register(
               @Valid @RequestBody RegisterRequest request) {
          AuthResponse data = authService.register(request);
          return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Registration successful", data));
     }

     @PostMapping("/login")
     public ResponseEntity<ApiResponse<AuthResponse>> login(
               @Valid @RequestBody LoginRequest request) {
          AuthResponse data = authService.login(request);
          return ResponseEntity.ok(ApiResponse.ok("Login successful", data));
     }

     @PostMapping("/refresh")
     public ResponseEntity<ApiResponse<AuthResponse>> refresh(
               @RequestHeader("Authorization") String authHeader) {

          String refreshToken = authHeader.startsWith("Bearer ")
                    ? authHeader.substring(7)
                    : authHeader;

          RefreshToken token = refreshTokenService.findByToken(refreshToken);
          refreshTokenService.verifyExpiration(token);

          User user = token.getUser();
          String newAccessToken = jwtUtil.generateToken(
                    user.getEmail(),
                    user.getRole().name(),
                    user.getTenant().getId());

          AuthResponse response = AuthResponse.builder()
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .tenantName(user.getTenant().getName())
                    .tenantDomain(user.getTenant().getDomain())
                    .accessToken(newAccessToken)
                    .type("Bearer")
                    .refreshToken(token.getToken())
                    .build();

          return ResponseEntity.ok(ApiResponse.ok("Token refreshed", response));
     }

     @PostMapping("/logout")
     public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserDetails userDetails) {
          refreshTokenService.deleteByUser(userDetails.getUsername());
          return ResponseEntity.ok(ApiResponse.ok("Logged out successfully", null));
     }
}