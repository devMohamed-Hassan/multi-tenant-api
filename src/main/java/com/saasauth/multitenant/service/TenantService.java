package com.saasauth.multitenant.service;

import org.springframework.stereotype.Service;

import com.saasauth.multitenant.dto.TenantResponse;
import com.saasauth.multitenant.dto.TenantUpdateRequest;
import com.saasauth.multitenant.model.Tenant;
import com.saasauth.multitenant.model.User;
import com.saasauth.multitenant.repository.TenantRepository;
import com.saasauth.multitenant.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenantService {

     private final TenantRepository tenantRepository;
     private final UserRepository userRepository;

     public TenantResponse getMyTenant(String email) {
          User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
          return mapToResponse(user.getTenant());
     }

     public TenantResponse updateTenant(String email, TenantUpdateRequest request) {
          User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

          Tenant tenant = user.getTenant();

          if (request.getName() != null)
               tenant.setName(request.getName());
          if (request.getDomain() != null)
               tenant.setDomain(request.getDomain());

          tenantRepository.save(tenant);
          return mapToResponse(tenant);
     }

     public void deleteTenant(String email) {
          User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
          tenantRepository.delete(user.getTenant());
     }

     private TenantResponse mapToResponse(Tenant tenant) {
          return TenantResponse.builder()
                    .id(tenant.getId())
                    .name(tenant.getName())
                    .domain(tenant.getDomain())
                    .userCount(tenant.getUsers() != null ? tenant.getUsers().size() : 0)
                    .build();
     }
}