package com.saasauth.multitenant.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saasauth.multitenant.dto.ChangePasswordRequest;
import com.saasauth.multitenant.dto.UpdateProfileRequest;
import com.saasauth.multitenant.dto.UpdateRoleRequest;
import com.saasauth.multitenant.dto.UserResponse;
import com.saasauth.multitenant.model.User;
import com.saasauth.multitenant.repository.RefreshTokenRepository;
import com.saasauth.multitenant.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

     private final UserRepository userRepository;
     private final PasswordEncoder passwordEncoder;
     private final RefreshTokenRepository refreshTokenRepository;
     private final EmailService emailService;

     public UserResponse getByEmail(String email) {
          User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
          return mapToResponse(user);
     }

     public List<UserResponse> getAllByTenant(String email) {
          User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
          return userRepository.findAllByTenant(user.getTenant())
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
     }

     public void changePassword(String email, ChangePasswordRequest request) {
          if (!request.getNewPassword().equals(request.getConfirmPassword())) {
               throw new RuntimeException("Passwords do not match");
          }

          User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

          if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
               throw new RuntimeException("Current password is incorrect");
          }

          user.setPassword(passwordEncoder.encode(request.getNewPassword()));
          userRepository.save(user);

          emailService.sendPasswordChangedEmail(user.getEmail(), user.getName());
     }

     public UserResponse updateUserRole(Long userId, UpdateRoleRequest request, String adminEmail) {
          User admin = userRepository.findByEmail(adminEmail)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

          User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

          if (!user.getTenant().getId().equals(admin.getTenant().getId())) {
               throw new RuntimeException("Access denied: user not in your tenant");
          }

          user.setRole(request.getRole());
          userRepository.save(user);
          return mapToResponse(user);
     }

     @Transactional
     public void deleteUser(Long userId, String adminEmail) {
          User admin = userRepository.findByEmail(adminEmail)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

          User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

          if (!user.getTenant().getId().equals(admin.getTenant().getId())) {
               throw new RuntimeException("Access denied: user not in your tenant");
          }
          
          if (user.getId().equals(admin.getId())) {
               throw new RuntimeException("You cannot delete your own account");
          }

          refreshTokenRepository.deleteByUser(user);
          userRepository.delete(user);
     }

     public UserResponse updateProfile(String email, UpdateProfileRequest request) {
          User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

          user.setName(request.getName());
          userRepository.save(user);
          return mapToResponse(user);
     }

     private UserResponse mapToResponse(User user) {
          return UserResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .tenantName(user.getTenant().getName())
                    .tenantDomain(user.getTenant().getDomain())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .createdBy(user.getCreatedBy())
                    .build();
     }
}