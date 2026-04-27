package com.saasauth.multitenant.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

     private final JwtFilter jwtFilter;
     private final RateLimitFilter rateLimitFilter;
     private final CustomUserDetailsService userDetailsService;

     @Bean
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
          http
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(session -> session
                              .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth
                              .dispatcherTypeMatchers(
                                        jakarta.servlet.DispatcherType.ERROR,
                                        jakarta.servlet.DispatcherType.FORWARD)
                              .permitAll()
                              .requestMatchers(
                                        "/api/v1/auth/**",
                                        "/api/v1/auth/password/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/v3/api-docs.yaml",
                                        "/webjars/**")
                              .permitAll()
                              .anyRequest().authenticated())
                    .exceptionHandling(ex -> ex
                              .authenticationEntryPoint((request, response, authException) -> {
                                   response.setContentType("application/json");
                                   response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                   response.getWriter().write(
                                             "{\"success\":false,\"message\":\"Access denied: missing or invalid token\",\"data\":null}");
                              })
                              .accessDeniedHandler((request, response, accessDeniedException) -> {
                                   response.setContentType("application/json");
                                   response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                   response.getWriter().write(
                                             "{\"success\":false,\"message\":\"Access denied: insufficient permissions\",\"data\":null}");
                              }))
                    .userDetailsService(userDetailsService)
                    .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

          return http.build();
     }

     @Bean
     public PasswordEncoder passwordEncoder() {
          return new BCryptPasswordEncoder();
     }

     @Bean
     public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
          return config.getAuthenticationManager();
     }
}