package com.myhome.repositories;

import com.myhome.domain.SecurityToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Extends Spring Data JPA's JpaRepository for database operations on SecurityToken
 * entities.
 */
public interface SecurityTokenRepository extends JpaRepository<SecurityToken, Long> {
}
