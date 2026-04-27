package com.saasauth.multitenant.security;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(1)
public class RequestLoggingFilter extends OncePerRequestFilter {

     @Override
     protected void doFilterInternal(HttpServletRequest request,
               HttpServletResponse response,
               FilterChain filterChain)
               throws ServletException, IOException {

          long startTime = System.currentTimeMillis();

          filterChain.doFilter(request, response);

          long duration = System.currentTimeMillis() - startTime;

          log.info("[{}] {} {} - {} ({}ms)",
                    request.getRemoteAddr(),
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);
     }
}