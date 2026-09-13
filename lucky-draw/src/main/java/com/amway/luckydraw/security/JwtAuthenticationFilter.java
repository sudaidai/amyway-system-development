package com.amway.luckydraw.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwt;

  public JwtAuthenticationFilter(JwtService jwt) {
    this.jwt = jwt;
  }

  protected void doFilterInternal(
      HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String h = req.getHeader("Authorization");
    if (h != null && h.startsWith("Bearer ")) {
      try {
        AuthenticatedPrincipal p = jwt.parse(h.substring(7));
        var authorities =
            p.roles().stream()
                .map(r -> new SimpleGrantedAuthority(r.startsWith("ROLE_") ? r : "ROLE_" + r))
                .toList();
        SecurityContextHolder.getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken(p, null, authorities));
      } catch (Exception e) {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        res.getWriter()
            .write(
                "{\"code\":\"UNAUTHORIZED\",\"message\":\"Invalid or expired token\",\"timestamp\":\""
                    + java.time.Instant.now()
                    + "\"}");
        return;
      }
    }
    chain.doFilter(req, res);
  }
}
