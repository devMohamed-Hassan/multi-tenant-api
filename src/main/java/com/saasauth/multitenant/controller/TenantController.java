package com.saasauth.multitenant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saasauth.multitenant.dto.ApiResponse;
import com.saasauth.multitenant.dto.TenantResponse;
import com.saasauth.multitenant.service.TenantService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
public class TenantController {

     private final TenantService tenantService;

     @GetMapping
     public ResponseEntity<ApiResponse<TenantResponse>> getMyTenant(
               @AuthenticationPrincipal UserDetails userDetails) {
          TenantResponse tenant = tenantService.getMyTenant(userDetails.getUsername());
          return ResponseEntity.ok(ApiResponse.ok("Tenant fetched", tenant));
     }

     @PutMapping
     @PreAuthorize("hasAuthority('ROLE_ADMIN')")
     public ResponseEntity<ApiResponse<TenantResponse>> updateTenant(
               @AuthenticationPrincipal UserDetails userDetails,
               @RequestBody TenantResponse request) {
          TenantResponse tenant = tenantService.updateTenant(userDetails.getUsername(), request);
          return ResponseEntity.ok(ApiResponse.ok("Tenant updated", tenant));
     }

     @DeleteMapping
     @PreAuthorize("hasAuthority('ROLE_ADMIN')")
     public ResponseEntity<ApiResponse<Void>> deleteTenant(
               @AuthenticationPrincipal UserDetails userDetails) {
          tenantService.deleteTenant(userDetails.getUsername());
          return ResponseEntity.ok(ApiResponse.ok("Tenant deleted", null));
     }
}