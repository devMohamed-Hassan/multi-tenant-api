package com.saasauth.multitenant.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasauth.multitenant.model.BlacklistedToken;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
     boolean existsByToken(String token);

     void deleteByExpiryDateBefore(Instant now);
}