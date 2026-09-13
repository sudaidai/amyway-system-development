package com.amway.luckydraw.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final SecretKey key;
  private final Duration ttl;

  public JwtService(
      @Value("${app.security.jwt-secret}") String secret,
      @Value("${app.security.jwt-ttl:PT1H}") Duration ttl) {
    if (secret.length() < 32)
      throw new IllegalArgumentException("JWT secret must contain at least 32 characters");
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.ttl = ttl;
  }

  public AuthenticatedPrincipal parse(String token) {
    Claims c = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    List<?> raw = c.get("roles", List.class);
    Set<String> roles = new HashSet<>();
    if (raw != null) raw.forEach(x -> roles.add(String.valueOf(x)));
    return new AuthenticatedPrincipal(c.getSubject(), Set.copyOf(roles));
  }

  public String generateToken(String userId, String... roles) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(userId)
        .claim("roles", List.of(roles))
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(ttl)))
        .signWith(key)
        .compact();
  }
}
