package com.saasauth.multitenant.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.saasauth.multitenant.dto.AuthResponse;
import com.saasauth.multitenant.dto.LoginRequest;
import com.saasauth.multitenant.dto.RegisterRequest;
import com.saasauth.multitenant.exception.UserAlreadyExistsException;
import com.saasauth.multitenant.model.RefreshToken;
import com.saasauth.multitenant.model.Role;
import com.saasauth.multitenant.model.Tenant;
import com.saasauth.multitenant.model.User;
import com.saasauth.multitenant.repository.TenantRepository;
import com.saasauth.multitenant.repository.UserRepository;
import com.saasauth.multitenant.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

     private final UserRepository userRepository;
     private final TenantRepository tenantRepository;
     private final PasswordEncoder passwordEncoder;
     private final JwtUtil jwtUtil;
     private final RefreshTokenService refreshTokenService;

     public AuthResponse register(RegisterRequest request) {
          if (userRepository.existsByEmail(request.getEmail())) {
               throw new UserAlreadyExistsException(request.getEmail());
          }

          Tenant tenant = tenantRepository.findByDomain(request.getTenantDomain())
                    .orElseGet(() -> {
                         Tenant newTenant = new Tenant();
                         newTenant.setName(request.getTenantName());
                         newTenant.setDomain(request.getTenantDomain());
                         return tenantRepository.save(newTenant);
                    });

          User user = new User();
          user.setName(request.getName());
          user.setEmail(request.getEmail());
          user.setPassword(passwordEncoder.encode(request.getPassword()));
          user.setRole(Role.ROLE_USER);
          user.setTenant(tenant);
          userRepository.save(user);

          String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), tenant.getId());
          RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

          return AuthResponse.builder()
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .tenantName(tenant.getName())
                    .tenantDomain(tenant.getDomain())
                    .accessToken(token)
                    .type("Bearer")
                    .refreshToken(refreshToken.getToken())
                    .build();
     }

     public AuthResponse login(LoginRequest request) {
          Tenant tenant = tenantRepository.findByDomain(request.getTenantDomain())
                    .orElseThrow(() -> new RuntimeException("Tenant not found"));

          User user = userRepository.findByEmailAndTenant(request.getEmail(), tenant)
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));

          if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
               throw new RuntimeException("Invalid credentials");
          }

          String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), tenant.getId());
          RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

          return AuthResponse.builder()
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .tenantName(tenant.getName())
                    .tenantDomain(tenant.getDomain())
                    .accessToken(token)
                    .type("Bearer")
                    .refreshToken(refreshToken.getToken())
                    .build();
     }
}