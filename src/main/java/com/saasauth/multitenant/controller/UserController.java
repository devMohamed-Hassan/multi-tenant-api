package com.saasauth.multitenant.controller;

import com.saasauth.multitenant.dto.ApiResponse;
import com.saasauth.multitenant.dto.UserResponse;
import com.saasauth.multitenant.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

     private final UserService userService;

     @GetMapping("/me")
     public ResponseEntity<ApiResponse<UserResponse>> getMe(@AuthenticationPrincipal UserDetails userDetails) {
          UserResponse user = userService.getByEmail(userDetails.getUsername());
          return ResponseEntity.ok(ApiResponse.ok("User fetched", user));
     }

     @GetMapping
     @PreAuthorize("hasAuthority('ROLE_ADMIN')")
     public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
               @AuthenticationPrincipal UserDetails userDetails) {
          List<UserResponse> users = userService.getAllByTenant(userDetails.getUsername());
          return ResponseEntity.ok(ApiResponse.ok("Users fetched", users));
     }
}