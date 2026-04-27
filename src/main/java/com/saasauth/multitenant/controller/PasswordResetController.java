package com.saasauth.multitenant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saasauth.multitenant.dto.ApiResponse;
import com.saasauth.multitenant.dto.ConfirmOtpRequest;
import com.saasauth.multitenant.dto.ForgotPasswordRequest;
import com.saasauth.multitenant.dto.OtpTokenResponse;
import com.saasauth.multitenant.dto.ResetPasswordRequest;
import com.saasauth.multitenant.dto.ResetTokenResponse;
import com.saasauth.multitenant.service.PasswordResetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Password Reset", description = "Forgot password, OTP verification and reset")
@RestController
@RequestMapping("/api/v1/auth/password")
@RequiredArgsConstructor
public class PasswordResetController {

     private final PasswordResetService passwordResetService;

     @Operation(summary = "Send OTP to email — returns otpToken in response body")
     @PostMapping("/forgot")
     public ResponseEntity<ApiResponse<OtpTokenResponse>> forgotPassword(
               @Valid @RequestBody ForgotPasswordRequest request) {
          String otpToken = passwordResetService.forgotPassword(request.getEmail());
          return ResponseEntity.ok(ApiResponse.ok(
                    "OTP sent to your email. Valid for 15 minutes.",
                    new OtpTokenResponse(otpToken)));
     }

     @Operation(summary = "Verify OTP — send otpToken as Bearer in Authorization header + OTP in body — returns resetToken in response body")
     @PostMapping("/confirm-otp")
     public ResponseEntity<ApiResponse<ResetTokenResponse>> confirmOtp(
               @RequestHeader("Authorization") String authHeader,
               @Valid @RequestBody ConfirmOtpRequest request) {
          String otpToken = extractToken(authHeader);
          String resetToken = passwordResetService.confirmOtp(otpToken, request.getOtp());
          return ResponseEntity.ok(ApiResponse.ok(
                    "OTP verified successfully.",
                    new ResetTokenResponse(resetToken)));
     }

     @Operation(summary = "Reset password — send resetToken as Bearer in Authorization header + new password in body")
     @PostMapping("/reset")
     public ResponseEntity<ApiResponse<Void>> resetPassword(
               @RequestHeader("Authorization") String authHeader,
               @Valid @RequestBody ResetPasswordRequest request) {
          String resetToken = extractToken(authHeader);
          passwordResetService.resetPassword(resetToken, request);
          return ResponseEntity.ok(ApiResponse.ok("Password has been reset successfully.", null));
     }

     @Operation(summary = "Resend OTP — returns new otpToken in response body")
     @PostMapping("/resend-otp")
     public ResponseEntity<ApiResponse<OtpTokenResponse>> resendOtp(
               @Valid @RequestBody ForgotPasswordRequest request) {
          String otpToken = passwordResetService.resendOtp(request.getEmail());
          return ResponseEntity.ok(ApiResponse.ok(
                    "New OTP sent to your email.",
                    new OtpTokenResponse(otpToken)));
     }

     private String extractToken(String authHeader) {
          if (authHeader == null || !authHeader.startsWith("Bearer ")) {
               throw new RuntimeException("Authorization header must be: Bearer <token>");
          }
          return authHeader.substring(7);
     }
}