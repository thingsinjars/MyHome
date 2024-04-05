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
 * represents a user account in a system with attributes such as name, user ID, email,
 * and encrypted password, and relationships with communities and security tokens.
 * Fields:
 * 	- name (String): in the User class represents a string value that identifies the
 * user's personal name.
 * 	- userId (String): represents a unique identifier for a user in the system.
 * 	- email (String): in the User class stores a unique string value representing an
 * email address associated with the user account.
 * 	- emailConfirmed (boolean): indicates whether an user's email address has been
 * confirmed through a verification process.
 * 	- encryptedPassword (String): in the User class represents an encrypted string
 * of password data.
 * 	- communities (Set<Community>): in the User class represents a many-to-many
 * relationship between the User entity and the Community entity, with the User entity
 * having multiple communities and each community having multiple Users as members.
 * 	- userTokens (Set<SecurityToken>): represents a set of SecurityTokens associated
 * with a single User entity in the provided Java code.
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


