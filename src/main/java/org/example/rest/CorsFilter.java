package org.example.rest;


import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.service.AuthService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;

@WebFilter
@Component
public class CorsFilter implements jakarta.servlet.Filter {

  @Inject
  private AuthService authService;

  @Override
  public void doFilter(final ServletRequest request,
                       final ServletResponse response,
                       final FilterChain chain) throws IOException, ServletException {
    final HttpServletResponse res = (HttpServletResponse) response;
    final HttpServletRequest req = (HttpServletRequest) request;
    String authHeader = req.getHeader("authorization");
    authService.setAuthContext(authHeader);
    res.addHeader("Access-Control-Allow-Origin", "*");
    res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH");
    res.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }
}