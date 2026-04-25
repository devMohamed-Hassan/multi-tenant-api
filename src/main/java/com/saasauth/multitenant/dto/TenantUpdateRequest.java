package com.saasauth.multitenant.dto;

import lombok.Data;

@Data
public class TenantUpdateRequest {
     private String name;
     private String domain;
}