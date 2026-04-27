package com.saasauth.multitenant.dto;

import java.time.Instant;

import com.saasauth.multitenant.model.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
     private Long id;
     private String name;
     private String email;
     private String role;
     private String tenantName;
     private String tenantDomain;
     private Instant createdAt;
     private Instant updatedAt;
     private String createdBy;

     public static UserResponse from(User user) {
          return UserResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .tenantName(user.getTenant().getName())
                    .tenantDomain(user.getTenant().getDomain())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .createdBy(user.getCreatedBy())
                    .build();
     }
}