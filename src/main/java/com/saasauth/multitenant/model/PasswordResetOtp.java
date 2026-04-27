package com.saasauth.multitenant.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "password_reset_otps")
public class PasswordResetOtp {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @Column(nullable = false)
     private String otp;

     @Column(nullable = false, unique = true)
     private String resetToken;

     @ManyToOne
     @JoinColumn(name = "user_id", nullable = false)
     private User user;

     @Column(nullable = false)
     private Instant expiryDate;

     private boolean verified;
}