package org.example.framework.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.framework.attribute.ContextAttributes;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.*;
import org.example.framework.util.FilterHelper;

import java.io.IOException;
import java.util.Arrays;

public class CookieAuthenticationFilter extends HttpFilter {
  private AuthenticationProvider provider;

  @Override
  public void init(FilterConfig config) throws ServletException {
    super.init(config);
    provider = ((AuthenticationProvider) getServletContext().getAttribute(ContextAttributes.AUTH_PROVIDER_ATTR));
  }

  @Override
  protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
    if (!FilterHelper.authenticationIsRequired(req)) {
      super.doFilter(req, res, chain);
      return;
    }

    final var cookies = req.getCookies();

    if (cookies == null) {
      super.doFilter(req, res, chain);
      return;
    }
    final var token = Arrays.stream(cookies)
            .filter(s -> s.getName().equals("token"))
            .map(Cookie::getValue)
            .findFirst();

    if (token.isEmpty()) {
      super.doFilter(req, res, chain);
      return;
    }

    try {
      final var authentication = provider.authenticate(new TokenAuthentication(token.get(), null));
      req.setAttribute(RequestAttributes.AUTH_ATTR, authentication);
    } catch (AuthenticationException e) {
      res.sendError(401);
      return;
    }

    super.doFilter(req, res, chain);
  }

}
