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

package com.myhome.services.springdatajpa;

import com.myhome.domain.CommunityHouse;
import com.myhome.domain.HouseMember;
import com.myhome.repositories.CommunityHouseRepository;
import com.myhome.repositories.HouseMemberDocumentRepository;
import com.myhome.repositories.HouseMemberRepository;
import com.myhome.services.HouseService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Provides data access and manipulation services for houses and their members using
 * Spring Data JPA repositories.
 */
@RequiredArgsConstructor
@Service
public class HouseSDJpaService implements HouseService {
  private final HouseMemberRepository houseMemberRepository;
  private final HouseMemberDocumentRepository houseMemberDocumentRepository;
  private final CommunityHouseRepository communityHouseRepository;

  /**
   * Generates a random unique identifier as a string.
   * It utilizes the `UUID` class to create a random UUID, which is then converted to
   * a string.
   *
   * @returns a random 128-bit UUID in the form of a 32-character hexadecimal string.
   */
  private String generateUniqueId() {
    return UUID.randomUUID().toString();
  }

  /**
   * Returns a set of all community houses from the database. It retrieves all community
   * houses from the repository, adds them to a set, and returns the set. The set is
   * used to store unique community houses, eliminating duplicates.
   *
   * @returns a set containing all CommunityHouse objects retrieved from the communityHouseRepository.
   */
  @Override
  public Set<CommunityHouse> listAllHouses() {
    Set<CommunityHouse> communityHouses = new HashSet<>();
    communityHouseRepository.findAll().forEach(communityHouses::add);
    return communityHouses;
  }

  /**
   * Retrieves a list of all community houses from the database and returns it as a
   * set, utilizing pagination for efficient data retrieval.
   *
   * @param pageable pagination criteria for the database query, allowing the results
   * to be split into pages with specified sizes and offsets.
   *
   * @returns a set of all community houses in the database, paginated according to the
   * input.
   */
  @Override
  public Set<CommunityHouse> listAllHouses(Pageable pageable) {
    Set<CommunityHouse> communityHouses = new HashSet<>();
    communityHouseRepository.findAll(pageable).forEach(communityHouses::add);
    return communityHouses;
  }

  /**
   * Adds new house members to a community house, generates unique IDs for the members,
   * associates them with the house and the community house, and saves the changes to
   * the database.
   *
   * @param houseId identifier of the community house for which members are being added.
   *
   * @param houseMembers set of house members to be added to a community house with the
   * specified `houseId`.
   *
   * Contain a set of HouseMember objects.
   * Each HouseMember has a unique memberId and a reference to a CommunityHouse.
   *
   * @returns a set of house members that were successfully saved to the database.
   *
   * Contain a set of house members.
   */
  @Override public Set<HouseMember> addHouseMembers(String houseId, Set<HouseMember> houseMembers) {
    Optional<CommunityHouse> communityHouseOptional =
        communityHouseRepository.findByHouseIdWithHouseMembers(houseId);
    return communityHouseOptional.map(communityHouse -> {
      Set<HouseMember> savedMembers = new HashSet<>();
      houseMembers.forEach(member -> member.setMemberId(generateUniqueId()));
      houseMembers.forEach(member -> member.setCommunityHouse(communityHouse));
      houseMemberRepository.saveAll(houseMembers).forEach(savedMembers::add);

      communityHouse.getHouseMembers().addAll(savedMembers);
      communityHouseRepository.save(communityHouse);
      return savedMembers;
    }).orElse(new HashSet<>());
  }

  /**
   * Removes a specified member from a community house with the given house ID, updates
   * the community house and member records accordingly, and returns true if the member
   * is found and removed, or false otherwise.
   *
   * @param houseId identifier of the community house from which a member is to be deleted.
   *
   * @param memberId identifier of the member to be removed from the specified community
   * house.
   *
   * @returns a boolean indicating whether the member was successfully removed from the
   * specified house.
   */
  @Override
  public boolean deleteMemberFromHouse(String houseId, String memberId) {
    Optional<CommunityHouse> communityHouseOptional =
        communityHouseRepository.findByHouseIdWithHouseMembers(houseId);
    return communityHouseOptional.map(communityHouse -> {
      boolean isMemberRemoved = false;
      if (!CollectionUtils.isEmpty(communityHouse.getHouseMembers())) {
        Set<HouseMember> houseMembers = communityHouse.getHouseMembers();
        for (HouseMember member : houseMembers) {
          if (member.getMemberId().equals(memberId)) {
            houseMembers.remove(member);
            communityHouse.setHouseMembers(houseMembers);
            communityHouseRepository.save(communityHouse);
            member.setCommunityHouse(null);
            houseMemberRepository.save(member);
            isMemberRemoved = true;
            break;
          }
        }
      }
      return isMemberRemoved;
    }).orElse(false);
  }

  /**
   * Returns the details of a community house by its ID, utilizing the `communityHouseRepository`
   * to find the corresponding entity. The result is wrapped in an `Optional` to indicate
   * the presence or absence of the house details.
   *
   * @param houseId identifier of the CommunityHouse entity for which details are to
   * be retrieved.
   *
   * @returns an Optional containing a CommunityHouse object if found, otherwise an
   * empty Optional.
   */
  @Override
  public Optional<CommunityHouse> getHouseDetailsById(String houseId) {
    return communityHouseRepository.findByHouseId(houseId);
  }

  /**
   * Retrieves a list of house members associated with a specified house ID, paginated
   * according to the provided `Pageable` object, and returns it as an `Optional` value.
   *
   * @param houseId identifier of a community house for which house members are to be
   * retrieved.
   *
   * @param pageable pagination criteria used to retrieve a subset of data from the database.
   *
   * @returns An Optional containing a List of HouseMember objects.
   */
  @Override
  public Optional<List<HouseMember>> getHouseMembersById(String houseId, Pageable pageable) {
    return Optional.ofNullable(
        houseMemberRepository.findAllByCommunityHouse_HouseId(houseId, pageable)
    );
  }

  /**
   * Returns an optional list of house members for the given user ID,
   * filtered by community houses where the user is an admin,
   * paginated according to the provided pageable object.
   *
   * @param userId identifier of a user for whom house members are being retrieved from
   * the database.
   *
   * @param pageable pagination criteria, allowing for the retrieval of a subset of
   * house members based on the specified page size and number.
   *
   * @returns an Optional containing a list of HouseMember objects.
   */
  @Override
  public Optional<List<HouseMember>> listHouseMembersForHousesOfUserId(String userId,
      Pageable pageable) {
    return Optional.ofNullable(
        houseMemberRepository.findAllByCommunityHouse_Community_Admins_UserId(userId, pageable)
    );
  }
}
