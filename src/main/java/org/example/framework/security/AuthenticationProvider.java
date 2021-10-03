package org.example.framework.security;

public interface AuthenticationProvider {
  Authentication authenticate(Authentication authentication) throws AuthenticationException;
  Authentication authenticate(String username, String password) throws AuthenticationException;
}
