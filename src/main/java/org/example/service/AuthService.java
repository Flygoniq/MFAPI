package org.example.service;

import org.springframework.stereotype.Component;

@Component
public class AuthService {
  private String authContext;

  public String getAuthContext() {
    return authContext;
  }

  public void setAuthContext(String authContext) {
    this.authContext = authContext;
  }
}
