package com.saasauth.multitenant.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saasauth.multitenant.dto.ApiResponse;
import com.saasauth.multitenant.dto.UserResponse;
import com.saasauth.multitenant.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

     private final UserService userService;

     @Operation(summary = "Get current user profile")
     @GetMapping("/me")
     public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal UserDetails userDetails) {
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
}