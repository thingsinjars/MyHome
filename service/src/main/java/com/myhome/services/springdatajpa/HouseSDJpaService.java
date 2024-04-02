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

@RequiredArgsConstructor
@Service
public class HouseSDJpaService implements HouseService {
  private final HouseMemberRepository houseMemberRepository;
  private final HouseMemberDocumentRepository houseMemberDocumentRepository;
  private final CommunityHouseRepository communityHouseRepository;

  /**
   * generates a unique, 128-bit universally unique identifier (UUID) using the
   * `UUID.randomUUID()` method.
   * 
   * @returns a unique alphanumeric string of a fixed length, generated using the `UUID`
   * class.
   */
  private String generateUniqueId() {
    return UUID.randomUUID().toString();
  }

  /**
   * retrieves a set of all community houses from the database using the `findAll()`
   * method of the `communityHouseRepository`.
   * 
   * @returns a set of all community houses stored in the database.
   */
  @Override
  public Set<CommunityHouse> listAllHouses() {
    Set<CommunityHouse> communityHouses = new HashSet<>();
    communityHouseRepository.findAll().forEach(communityHouses::add);
    return communityHouses;
  }

  /**
   * retrieves a pageable set of CommunityHouse objects from the repository and returns
   * them in a new Set object.
   * 
   * @param pageable page of results that the method should retrieve, allowing for
   * pagination and efficient retrieval of a subset of the list of community houses.
   * 
   * @returns a set of `CommunityHouse` objects.
   */
  @Override
  public Set<CommunityHouse> listAllHouses(Pageable pageable) {
    Set<CommunityHouse> communityHouses = new HashSet<>();
    communityHouseRepository.findAll(pageable).forEach(communityHouses::add);
    return communityHouses;
  }

  /**
   * adds new house members to an existing community house by creating unique member
   * IDs, saving them, and then adding them to the community house's member list.
   * 
   * @param houseId unique identifier of the house for which new members are being added.
   * 
   * @param houseMembers existing members of the house to be added or updated, and it
   * is used to generate unique member IDs and link the members to the corresponding
   * community house.
   * 
   * @returns a set of house members associated with the specified house ID.
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
   * deletes a member from a community house by finding the house and its members,
   * removing the specified member from the house members list, saving the changes, and
   * updating the member's reference to the community house.
   * 
   * @param houseId ID of the community house for which the member is being removed.
   * 
   * @param memberId member ID of the member to be removed from the community house.
   * 
   * @returns a boolean value indicating whether the member was removed from the house
   * successfully.
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
   * retrieves Community House details by Id from the repository.
   * 
   * @param houseId unique identifier of the community house to retrieve details for.
   * 
   * @returns an Optional containing the details of the specified house, if it exists
   * in the database.
   */
  @Override
  public Optional<CommunityHouse> getHouseDetailsById(String houseId) {
    return communityHouseRepository.findByHouseId(houseId);
  }

  /**
   * queries the `houseMemberRepository` to retrieve a list of `House Member` objects
   * associated with a given `houseId`. The function returns an `Optional` containing
   * the retrieved list, or `null` if no matches are found.
   * 
   * @param houseId identifier of the community house for which the list of members is
   * being retrieved.
   * 
   * @param pageable page number and the desired page size for the result set of house
   * members returned by the function.
   * 
   * @returns a paginated list of house members associated with the specified house ID.
   */
  @Override
  public Optional<List<HouseMember>> getHouseMembersById(String houseId, Pageable pageable) {
    return Optional.ofNullable(
        houseMemberRepository.findAllByCommunityHouse_HouseId(houseId, pageable)
    );
  }

  /**
   * retrieves a list of HouseMembers associated with a user's communities, paginated
   * by the input parameter.
   * 
   * @param userId ID of the user for whom the list of house members is being retrieved.
   * 
   * @param pageable pagination information for the House Member data, allowing for
   * efficient retrieval of a subset of the data within a specific page and limit.
   * 
   * @returns a list of `HouseMember` objects associated with the specified user ID.
   */
  @Override
  public Optional<List<HouseMember>> listHouseMembersForHousesOfUserId(String userId,
      Pageable pageable) {
    return Optional.ofNullable(
        houseMemberRepository.findAllByCommunityHouse_Community_Admins_UserId(userId, pageable)
    );
  }
}






















