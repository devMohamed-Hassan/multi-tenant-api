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
import com.saasauth.multitenant.dto.TenantUpdateRequest;
import com.saasauth.multitenant.service.TenantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Tenant", description = "Tenant info and management")
@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
public class TenantController {

     private final TenantService tenantService;

     @Operation(summary = "Get current user's tenant info")
     @GetMapping
     public ResponseEntity<ApiResponse<TenantResponse>> getMyTenant(
               @AuthenticationPrincipal UserDetails userDetails) {
          TenantResponse tenant = tenantService.getMyTenant(userDetails.getUsername());
          return ResponseEntity.ok(ApiResponse.ok("Tenant fetched", tenant));
     }

     @Operation(summary = "Update tenant name or domain - ADMIN only")
     @PutMapping
     @PreAuthorize("hasAuthority('ROLE_ADMIN')")
     public ResponseEntity<ApiResponse<TenantResponse>> updateTenant(
               @AuthenticationPrincipal UserDetails userDetails,
               @RequestBody TenantUpdateRequest request) {
          TenantResponse tenant = tenantService.updateTenant(userDetails.getUsername(), request);
          return ResponseEntity.ok(ApiResponse.ok("Tenant updated", tenant));
     }

     @Operation(summary = "Delete tenant and all its users - ADMIN only")
     @DeleteMapping
     @PreAuthorize("hasAuthority('ROLE_ADMIN')")
     public ResponseEntity<ApiResponse<Void>> deleteTenant(
               @AuthenticationPrincipal UserDetails userDetails) {
          tenantService.deleteTenant(userDetails.getUsername());
          return ResponseEntity.ok(ApiResponse.ok("Tenant deleted", null));
     }
}