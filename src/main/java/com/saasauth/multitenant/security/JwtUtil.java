package com.saasauth.multitenant.security;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

     @Value("${app.jwt.secret}")
     private String secret;

     @Value("${app.jwt.expiration}")
     private long expiration;

     private Key getSigningKey() {
          return Keys.hmacShaKeyFor(secret.getBytes());
     }

     public String generateToken(String email, String role, Long tenantId) {
          return Jwts.builder()
                    .setSubject(email)
                    .claim("role", role)
                    .claim("tenantId", tenantId)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
     }

     public String extractEmail(String token) {
          return getClaims(token).getSubject();
     }

     public String extractRole(String token) {
          return getClaims(token).get("role", String.class);
     }

     public Long extractTenantId(String token) {
          return getClaims(token).get("tenantId", Long.class);
     }

     public boolean isTokenValid(String token) {
          try {
               getClaims(token);
               return true;
          } catch (JwtException | IllegalArgumentException e) {
               return false;
          }
     }

     private Claims getClaims(String token) {
          return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
     }

     public Instant extractExpiration(String token) {
          return getClaims(token).getExpiration().toInstant();
     }
}