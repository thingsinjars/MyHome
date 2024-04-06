/*
 * Copyright 2020 Prathab Murugan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.myhome.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity identifying a valid user in the service.
 */
/**
 * in Java represents a valid user in the service and has attributes for name, userId,
 * email, email confirmed, encrypted password, communities, and user tokens, as well
 * as methods for retrieving and manipulating these attributes.
 * Fields:
 * 	- name (String): in the User entity represents a string value used to identify a
 * user within the system.
 * 	- userId (String): represents a unique identifier for each user in the system.
 * 	- email (String): in the User class represents an email address associated with
 * the user account.
 * 	- emailConfirmed (boolean): indicates whether the user's email address has been
 * confirmed through a verification process.
 * 	- encryptedPassword (String): stores an encrypted password for the user's account.
 * 	- communities (Set<Community>): is a ManyToMany relationship map between Users
 * and Communities, with Community objects being associated with multiple Users through
 * the admins attribute.
 * 	- userTokens (Set<SecurityToken>): in the User class represents a set of security
 * tokens associated with each user, which are linked to the user through the tokenOwner
 * field.
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false, of = {"userId", "email"})
@Entity
@With
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "User.communities",
        attributeNodes = {
            @NamedAttributeNode("communities"),
        }
    ),
    @NamedEntityGraph(
        name = "User.userTokens",
        attributeNodes = {
            @NamedAttributeNode("userTokens"),
        }
    )
})
public class User extends BaseEntity {
  @Column(nullable = false)
  private String name;
  @Column(unique = true, nullable = false)
  private String userId;
  @Column(unique = true, nullable = false)
  private String email;
  @Column(nullable = false)
  private boolean emailConfirmed = false;
  @Column(nullable = false)
  private String encryptedPassword;
  @ManyToMany(mappedBy = "admins", fetch = FetchType.LAZY)
  private Set<Community> communities = new HashSet<>();
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "tokenOwner")
  private Set<SecurityToken> userTokens = new HashSet<>();
}
