package com.myhome.services;

import com.myhome.domain.AuthenticationData;
import com.myhome.model.LoginRequest;

/**
 * Defines a contract for authentication services, specifically providing a login method.
 */
public interface AuthenticationService {
  AuthenticationData login(LoginRequest loginRequest);
}
