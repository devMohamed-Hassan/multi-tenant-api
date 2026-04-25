package com.saasauth.multitenant.dto;

import lombok.Data;

@Data
public class RefreshTokenRequest {
     private String refreshToken;
}