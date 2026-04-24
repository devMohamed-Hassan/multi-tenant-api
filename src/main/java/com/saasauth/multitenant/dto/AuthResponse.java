package com.saasauth.multitenant.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
     private String type;
     private String email;
     private String name;
     private String role;
     private String tenantName;
     private String tenantDomain;
     private String token;
}