package com.saasauth.multitenant.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.saasauth.multitenant.dto.AuthResponse;
import com.saasauth.multitenant.dto.LoginRequest;
import com.saasauth.multitenant.dto.RegisterRequest;
import com.saasauth.multitenant.exception.UserAlreadyExistsException;
import com.saasauth.multitenant.model.Role;
import com.saasauth.multitenant.model.Tenant;
import com.saasauth.multitenant.model.User;
import com.saasauth.multitenant.repository.TenantRepository;
import com.saasauth.multitenant.repository.UserRepository;
import com.saasauth.multitenant.security.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

     @Mock
     private UserRepository userRepository;
     @Mock
     private TenantRepository tenantRepository;
     @Mock
     private PasswordEncoder passwordEncoder;
     @Mock
     private JwtUtil jwtUtil;
     @Mock
     private RefreshTokenService refreshTokenService;

     @InjectMocks
     private AuthService authService;

     private Tenant tenant;
     private User user;
     private RegisterRequest registerRequest;
     private LoginRequest loginRequest;

     @BeforeEach
     void setUp() {
          tenant = new Tenant();
          tenant.setId(1L);
          tenant.setName("MyCompany");
          tenant.setDomain("mycompany.com");

          user = new User();
          user.setId(1L);
          user.setName("John");
          user.setEmail("john@example.com");
          user.setPassword("encodedPassword");
          user.setRole(Role.ROLE_USER);
          user.setTenant(tenant);

          registerRequest = new RegisterRequest();
          registerRequest.setName("John");
          registerRequest.setEmail("john@example.com");
          registerRequest.setPassword("123456");
          registerRequest.setTenantName("MyCompany");
          registerRequest.setTenantDomain("mycompany.com");

          loginRequest = new LoginRequest();
          loginRequest.setEmail("john@example.com");
          loginRequest.setPassword("123456");
          loginRequest.setTenantDomain("mycompany.com");
     }

     @Test
     void register_success() {
          when(userRepository.existsByEmail(anyString())).thenReturn(false);
          when(tenantRepository.findByDomain(anyString())).thenReturn(Optional.of(tenant));
          when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
          when(userRepository.save(any())).thenReturn(user);
          when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("jwt-token");
          when(refreshTokenService.createRefreshToken(anyString()))
                    .thenReturn(new com.saasauth.multitenant.model.RefreshToken());

          AuthResponse response = authService.register(registerRequest);

          assertNotNull(response);
          assertEquals("john@example.com", response.getEmail());
          assertEquals("John", response.getName());
          verify(userRepository).save(any(User.class));
     }

     @Test
     void register_emailAlreadyExists_throwsException() {
          when(userRepository.existsByEmail(anyString())).thenReturn(true);

          assertThrows(UserAlreadyExistsException.class,
                    () -> authService.register(registerRequest));

          verify(userRepository, never()).save(any());
     }

     @Test
     void login_success() {
          when(tenantRepository.findByDomain(anyString())).thenReturn(Optional.of(tenant));
          when(userRepository.findByEmailAndTenant(anyString(), any())).thenReturn(Optional.of(user));
          when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
          when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("jwt-token");
          when(refreshTokenService.createRefreshToken(anyString()))
                    .thenReturn(new com.saasauth.multitenant.model.RefreshToken());

          AuthResponse response = authService.login(loginRequest);

          assertNotNull(response);
          assertEquals("john@example.com", response.getEmail());
          assertEquals("Bearer", response.getType());
     }

     @Test
     void login_tenantNotFound_throwsException() {
          when(tenantRepository.findByDomain(anyString())).thenReturn(Optional.empty());

          assertThrows(RuntimeException.class,
                    () -> authService.login(loginRequest));
     }

     @Test
     void login_wrongPassword_throwsException() {
          when(tenantRepository.findByDomain(anyString())).thenReturn(Optional.of(tenant));
          when(userRepository.findByEmailAndTenant(anyString(), any())).thenReturn(Optional.of(user));
          when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

          assertThrows(RuntimeException.class,
                    () -> authService.login(loginRequest));
     }
}