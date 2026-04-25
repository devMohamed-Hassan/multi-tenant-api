package com.saasauth.multitenant.service;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saasauth.multitenant.model.BlacklistedToken;
import com.saasauth.multitenant.repository.BlacklistedTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

     private final BlacklistedTokenRepository blacklistedTokenRepository;

     public void blacklist(String token, Instant expiryDate) {
          BlacklistedToken blacklistedToken = new BlacklistedToken();
          blacklistedToken.setToken(token);
          blacklistedToken.setExpiryDate(expiryDate);
          blacklistedTokenRepository.save(blacklistedToken);
     }

     public boolean isBlacklisted(String token) {
          return blacklistedTokenRepository.existsByToken(token);
     }

     @Scheduled(fixedRate = 3600000)
     @Transactional
     public void cleanExpiredTokens() {
          blacklistedTokenRepository.deleteByExpiryDateBefore(Instant.now());
     }
}