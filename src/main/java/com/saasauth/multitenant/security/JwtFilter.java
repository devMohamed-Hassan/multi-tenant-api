package com.saasauth.multitenant.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.saasauth.multitenant.service.TokenBlacklistService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

     private final JwtUtil jwtUtil;
     private final CustomUserDetailsService userDetailsService;
     private final TokenBlacklistService tokenBlacklistService;

     @Override
     protected boolean shouldNotFilter(HttpServletRequest request) {
          String path = request.getServletPath();
          return path.startsWith("/swagger-ui") ||
                    path.startsWith("/v3/api-docs") ||
                    path.equals("/api/v1/auth/register") ||
                    path.equals("/api/v1/auth/login");
     }

     @Override
     protected void doFilterInternal(HttpServletRequest request,
               HttpServletResponse response,
               FilterChain filterChain)
               throws ServletException, IOException {

          String authHeader = request.getHeader("Authorization");

          if (authHeader != null && authHeader.startsWith("Bearer ")) {
               String token = authHeader.substring(7);

               if (jwtUtil.isTokenValid(token) && !tokenBlacklistService.isBlacklisted(token)) {
                    String email = jwtUtil.extractEmail(token);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                              userDetails,
                              null,
                              userDetails.getAuthorities());

                    authentication.setDetails(
                              new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
               }
          }

          filterChain.doFilter(request, response);
     }
}