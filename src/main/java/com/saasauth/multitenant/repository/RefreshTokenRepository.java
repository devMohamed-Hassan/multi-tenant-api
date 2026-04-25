package com.saasauth.multitenant.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasauth.multitenant.model.RefreshToken;
import com.saasauth.multitenant.model.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
     Optional<RefreshToken> findByToken(String token);

     void deleteByUser(User user);
}