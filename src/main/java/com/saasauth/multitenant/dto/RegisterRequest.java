package com.saasauth.multitenant.dto;

import lombok.Data;

@Data
public class RegisterRequest {
     private String name;
     private String email;
     private String password;
     private String tenantName;
     private String tenantDomain;
}