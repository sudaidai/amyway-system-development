package com.amway.luckydraw.security;

import java.security.Principal;
import java.util.Set;

public record AuthenticatedPrincipal(String userId, Set<String> roles) implements Principal {
  public String getName() {
    return userId;
  }
}
