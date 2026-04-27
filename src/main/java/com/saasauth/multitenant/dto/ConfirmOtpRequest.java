package com.saasauth.multitenant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmOtpRequest {

     @NotBlank(message = "OTP is required")
     private String otp;
}