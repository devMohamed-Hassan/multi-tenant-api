package com.saasauth.multitenant.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.saasauth.multitenant.dto.ChangePasswordRequest;
import com.saasauth.multitenant.dto.UserResponse;
import com.saasauth.multitenant.model.Role;
import com.saasauth.multitenant.model.Tenant;
import com.saasauth.multitenant.model.User;
import com.saasauth.multitenant.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

     @Mock
     private UserRepository userRepository;
     @Mock
     private PasswordEncoder passwordEncoder;

     @InjectMocks
     private UserService userService;

     private User user;
     private Tenant tenant;

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
     }

     @Test
     void getByEmail_success() {
          when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

          UserResponse response = userService.getByEmail("john@example.com");

          assertNotNull(response);
          assertEquals("john@example.com", response.getEmail());
          assertEquals("John", response.getName());
          assertEquals("ROLE_USER", response.getRole());
     }

     @Test
     void getByEmail_notFound_throwsException() {
          when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

          assertThrows(RuntimeException.class,
                    () -> userService.getByEmail("unknown@example.com"));
     }

     @Test
     void getAllByTenant_success() {
          when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
          when(userRepository.findAllByTenant(any())).thenReturn(List.of(user));

          List<UserResponse> users = userService.getAllByTenant("john@example.com");

          assertNotNull(users);
          assertEquals(1, users.size());
          assertEquals("john@example.com", users.get(0).getEmail());
     }

     @Test
     void changePassword_success() {
          ChangePasswordRequest request = new ChangePasswordRequest();
          request.setCurrentPassword("123456");
          request.setNewPassword("newpass123");
          request.setConfirmPassword("newpass123");

          when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
          when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
          when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

          assertDoesNotThrow(() -> userService.changePassword("john@example.com", request));
          verify(userRepository).save(any(User.class));
     }

     @Test
     void changePassword_passwordsDoNotMatch_throwsException() {
          ChangePasswordRequest request = new ChangePasswordRequest();
          request.setCurrentPassword("123456");
          request.setNewPassword("newpass123");
          request.setConfirmPassword("differentpass");

          assertThrows(RuntimeException.class,
                    () -> userService.changePassword("john@example.com", request));

          verify(userRepository, never()).save(any());
     }

     @Test
     void changePassword_wrongCurrentPassword_throwsException() {
          ChangePasswordRequest request = new ChangePasswordRequest();
          request.setCurrentPassword("wrongpass");
          request.setNewPassword("newpass123");
          request.setConfirmPassword("newpass123");

          when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
          when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

          assertThrows(RuntimeException.class,
                    () -> userService.changePassword("john@example.com", request));

          verify(userRepository, never()).save(any());
     }
}