package com.saasauth.multitenant.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saasauth.multitenant.model.RefreshToken;
import com.saasauth.multitenant.model.User;
import com.saasauth.multitenant.repository.RefreshTokenRepository;
import com.saasauth.multitenant.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

     private final RefreshTokenRepository refreshTokenRepository;
     private final UserRepository userRepository;

     private static final long REFRESH_TOKEN_EXPIRY_MS = 7 * 24 * 60 * 60 * 1000L;

     @Transactional
     public RefreshToken createRefreshToken(String email) {
          User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

          refreshTokenRepository.deleteByUser(user);
          refreshTokenRepository.flush();

          RefreshToken refreshToken = new RefreshToken();
          refreshToken.setToken(UUID.randomUUID().toString());
          refreshToken.setUser(user);
          refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRY_MS));

          return refreshTokenRepository.save(refreshToken);
     }

     public RefreshToken verifyExpiration(RefreshToken token) {
          if (token.getExpiryDate().isBefore(Instant.now())) {
               refreshTokenRepository.delete(token);
               throw new RuntimeException("Refresh token expired. Please login again.");
          }
          return token;
     }

     public RefreshToken findByToken(String token) {
          return refreshTokenRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Refresh token not found"));
     }

     @Transactional
     public void deleteByUser(String email) {
          User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
          refreshTokenRepository.deleteByUser(user);
     }
}