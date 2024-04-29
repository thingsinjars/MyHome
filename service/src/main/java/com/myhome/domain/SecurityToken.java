package com.myhome.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

/**
 * represents a secure token with a unique identifier and various attributes related
 * to its creation, expiration, and usage, along with a reference to the user who
 * owns the token.
 * Fields:
 * 	- tokenType (SecurityTokenType): in the SecurityToken class represents an enumeration
 * of possible types of security tokens.
 * 	- token (String): is of type SecurityTokenType and stores an enumerated value
 * representing the token's type.
 * 	- creationDate (LocalDate): represents the date when the security token was created.
 * 	- expiryDate (LocalDate): represents the date after which the SecurityToken is
 * no longer valid or usable.
 * 	- isUsed (boolean): indicates whether the security token has been used or not.
 * 	- tokenOwner (User): represents an entity (either a user or another object) that
 * owns or has access to the security token.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"tokenOwner"})
public class SecurityToken extends BaseEntity {
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private SecurityTokenType tokenType;
  @Column(nullable = false, unique = true)
  private String token;
  @Column(nullable = false)
  private LocalDate creationDate;
  @Column(nullable = false)
  private LocalDate expiryDate;
  private boolean isUsed;
  @ManyToOne
  private User tokenOwner;
}
