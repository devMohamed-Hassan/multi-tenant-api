package com.saasauth.multitenant.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasauth.multitenant.model.PasswordResetOtp;
import com.saasauth.multitenant.model.User;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
     Optional<PasswordResetOtp> findByResetToken(String resetToken);

     Optional<PasswordResetOtp> findByOtpAndUser(String otp, User user);

     void deleteByUser(User user);

     void deleteByExpiryDateBefore(Instant now);
}