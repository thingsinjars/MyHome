package com.myhome.controllers.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

/**
 * Represents a request for password recovery, encapsulating email, token, and new
 * password information.
 *
 * - email (String): is annotated with a constraint to ensure it is a valid email address.
 *
 * - token (String): is a string.
 *
 * - newPassword (String): is a string representing the new password.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ForgotPasswordRequest {
  @Email
  public String email;
  public String token;
  public String newPassword;
}
