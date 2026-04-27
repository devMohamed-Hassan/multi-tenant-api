package com.saasauth.multitenant.service;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saasauth.multitenant.dto.ResetPasswordRequest;
import com.saasauth.multitenant.model.PasswordResetOtp;
import com.saasauth.multitenant.model.User;
import com.saasauth.multitenant.repository.PasswordResetOtpRepository;
import com.saasauth.multitenant.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

     private final PasswordResetOtpRepository otpRepository;
     private final UserRepository userRepository;
     private final PasswordEncoder passwordEncoder;
     private final EmailService emailService;

     private static final long OTP_EXPIRY_MS = 15 * 60 * 1000L;

     @Transactional
     public String forgotPassword(String email) {
          User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

          otpRepository.deleteByUser(user);

          String otp = generateOtp();
          String otpToken = UUID.randomUUID().toString();

          PasswordResetOtp passwordResetOtp = new PasswordResetOtp();
          passwordResetOtp.setOtp(otp);
          passwordResetOtp.setResetToken(otpToken);
          passwordResetOtp.setUser(user);
          passwordResetOtp.setExpiryDate(Instant.now().plusMillis(OTP_EXPIRY_MS));
          passwordResetOtp.setVerified(false);
          otpRepository.save(passwordResetOtp);

          emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), otp);

          return otpToken;
     }

     @Transactional
     public String confirmOtp(String otpToken, String otp) {
          PasswordResetOtp otpEntity = otpRepository
                    .findByResetToken(otpToken)
                    .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

          if (!otpEntity.getOtp().equals(otp)) {
               throw new RuntimeException("Invalid OTP");
          }

          if (otpEntity.getExpiryDate().isBefore(Instant.now())) {
               otpRepository.delete(otpEntity);
               throw new RuntimeException("OTP has expired. Please request a new one.");
          }

          String resetToken = UUID.randomUUID().toString();
          otpEntity.setResetToken(resetToken);
          otpEntity.setVerified(true);
          otpRepository.save(otpEntity);

          return resetToken;
     }

     @Transactional
     public void resetPassword(String resetToken, ResetPasswordRequest request) {
          if (!request.getNewPassword().equals(request.getConfirmPassword())) {
               throw new RuntimeException("Passwords do not match");
          }

          PasswordResetOtp otpEntity = otpRepository
                    .findByResetToken(resetToken)
                    .orElseThrow(() -> new RuntimeException("Invalid reset token"));

          if (!otpEntity.isVerified()) {
               throw new RuntimeException("OTP not verified. Please verify OTP first.");
          }

          if (otpEntity.getExpiryDate().isBefore(Instant.now())) {
               otpRepository.delete(otpEntity);
               throw new RuntimeException("Reset token has expired. Please request a new one.");
          }

          User user = otpEntity.getUser();
          user.setPassword(passwordEncoder.encode(request.getNewPassword()));
          userRepository.save(user);

          otpRepository.delete(otpEntity);
          emailService.sendPasswordChangedEmail(user.getEmail(), user.getName());
     }

     @Transactional
     public String resendOtp(String email) {
          User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

          otpRepository.deleteByUser(user);

          String otp = generateOtp();
          String otpToken = UUID.randomUUID().toString();

          PasswordResetOtp passwordResetOtp = new PasswordResetOtp();
          passwordResetOtp.setOtp(otp);
          passwordResetOtp.setResetToken(otpToken);
          passwordResetOtp.setUser(user);
          passwordResetOtp.setExpiryDate(Instant.now().plusMillis(OTP_EXPIRY_MS));
          passwordResetOtp.setVerified(false);
          otpRepository.save(passwordResetOtp);

          emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), otp);

          return otpToken;
     }

     @Scheduled(fixedRate = 3600000)
     @Transactional
     public void cleanExpiredOtps() {
          otpRepository.deleteByExpiryDateBefore(Instant.now());
     }

     private String generateOtp() {
          return String.format("%06d", new Random().nextInt(999999));
     }
}