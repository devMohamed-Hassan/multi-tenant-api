package com.saasauth.multitenant.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saasauth.multitenant.dto.ApiResponse;
import com.saasauth.multitenant.dto.ChangePasswordRequest;
import com.saasauth.multitenant.dto.UserResponse;
import com.saasauth.multitenant.security.JwtUtil;
import com.saasauth.multitenant.service.RefreshTokenService;
import com.saasauth.multitenant.service.TokenBlacklistService;
import com.saasauth.multitenant.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Users", description = "User profile and tenant user management")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

     private final UserService userService;
     private final TokenBlacklistService tokenBlacklistService;
     private final RefreshTokenService refreshTokenService;
     private final JwtUtil jwtUtil;

     @Operation(summary = "Get current user profile")
     @GetMapping("/me")
     public ResponseEntity<ApiResponse<UserResponse>> getMe(
               @AuthenticationPrincipal UserDetails userDetails) {
          UserResponse user = userService.getByEmail(userDetails.getUsername());
          return ResponseEntity.ok(ApiResponse.ok("User fetched", user));
     }

     @Operation(summary = "Get all users in tenant - ADMIN only")
     @GetMapping
     @PreAuthorize("hasAuthority('ROLE_ADMIN')")
     public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
               @AuthenticationPrincipal UserDetails userDetails) {
          List<UserResponse> users = userService.getAllByTenant(userDetails.getUsername());
          return ResponseEntity.ok(ApiResponse.ok("Users fetched", users));
     }

     @Operation(summary = "Change current user password")
     @PutMapping("/me/password")
     public ResponseEntity<ApiResponse<Void>> changePassword(
               @AuthenticationPrincipal UserDetails userDetails,
               @Valid @RequestBody ChangePasswordRequest request,
               @RequestHeader("Authorization") String authHeader) {

          userService.changePassword(userDetails.getUsername(), request);

          String accessToken = authHeader.substring(7);
          Instant expiry = jwtUtil.extractExpiration(accessToken);
          tokenBlacklistService.blacklist(accessToken, expiry);

          refreshTokenService.deleteByUser(userDetails.getUsername());

          return ResponseEntity.ok(ApiResponse.ok("Password changed. Please login again.", null));
     }
}