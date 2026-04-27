package com.saasauth.multitenant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OtpTokenResponse {
     private String otpToken;
}