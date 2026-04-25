package com.saasauth.multitenant.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.saasauth.multitenant.dto.UserResponse;
import com.saasauth.multitenant.model.User;
import com.saasauth.multitenant.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

     private final UserRepository userRepository;

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

     private UserResponse mapToResponse(User user) {
          return UserResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .tenantName(user.getTenant().getName())
                    .tenantDomain(user.getTenant().getDomain())
                    .build();
     }
}