package com.saasauth.multitenant.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.saasauth.multitenant.dto.UserResponse;
import com.saasauth.multitenant.model.User;
import com.saasauth.multitenant.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

     private final UserRepository userRepository;

     public User getByEmail(String email) {
          return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
     }

     public UserResponse getProfileByEmail(String email) {
          return UserResponse.from(getByEmail(email));
     }

     public List<UserResponse> getAllByTenant(String email) {
          User user = getByEmail(email);
          return userRepository.findAllByTenant(user.getTenant())
                    .stream()
                    .map(UserResponse::from)
                    .toList();
     }
}