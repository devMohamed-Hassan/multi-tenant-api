package com.saasauth.multitenant.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TenantResponse {
     private Long id;
     private String name;
     private String domain;
     private int userCount;
}