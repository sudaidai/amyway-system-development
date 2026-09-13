package com.amway.luckydraw.security;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
  @Bean
  SecurityFilterChain security(HttpSecurity http, JwtAuthenticationFilter jwt) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            e ->
                e.authenticationEntryPoint(
                        (q, r, x) ->
                            error(
                                r,
                                HttpServletResponse.SC_UNAUTHORIZED,
                                "UNAUTHORIZED",
                                "Authentication is required"))
                    .accessDeniedHandler(
                        (q, r, x) ->
                            error(
                                r,
                                HttpServletResponse.SC_FORBIDDEN,
                                "FORBIDDEN",
                                "Insufficient permission")))
        .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(
            a ->
                a.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/health")
                    .permitAll()
                    .requestMatchers("/api/v1/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers("/api/v1/**")
                    .hasAnyRole("USER", "ADMIN")
                    .anyRequest()
                    .permitAll())
        .build();
  }

  private static void error(HttpServletResponse r, int status, String code, String message)
      throws java.io.IOException {
    r.setStatus(status);
    r.setContentType("application/json");
    r.getWriter()
        .printf(
            "{\"code\":\"%s\",\"message\":\"%s\",\"timestamp\":\"%s\"}",
            code, message, Instant.now());
  }
}
