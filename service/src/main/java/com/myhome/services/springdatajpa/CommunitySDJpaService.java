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

import com.myhome.controllers.dto.CommunityDto;
import com.myhome.controllers.dto.mapper.CommunityMapper;
import com.myhome.domain.Community;
import com.myhome.domain.CommunityHouse;
import com.myhome.domain.HouseMember;
import com.myhome.domain.User;
import com.myhome.repositories.CommunityHouseRepository;
import com.myhome.repositories.CommunityRepository;
import com.myhome.repositories.UserRepository;
import com.myhome.services.CommunityService;
import com.myhome.services.HouseService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Provides data access and management operations for communities, including creation,
 * retrieval, and deletion, as well as administration and house management.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CommunitySDJpaService implements CommunityService {
  private final CommunityRepository communityRepository;
  private final UserRepository communityAdminRepository;
  private final CommunityMapper communityMapper;
  private final CommunityHouseRepository communityHouseRepository;
  private final HouseService houseService;

  /**
   * Creates a new community based on the provided `CommunityDto`, generates a unique
   * ID, adds an admin user, and saves the community to the repository.
   *
   * @param communityDto data to be used for creating a new community, mapping its
   * properties to a `Community` object.
   *
   * @returns a saved `Community` object with a generated unique ID.
   */
  @Override
  public Community createCommunity(CommunityDto communityDto) {
    communityDto.setCommunityId(generateUniqueId());
    String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Community community = addAdminToCommunity(communityMapper.communityDtoToCommunity(communityDto),
        userId);
    Community savedCommunity = communityRepository.save(community);
    log.trace("saved community with id[{}] to repository", savedCommunity.getId());
    return savedCommunity;
  }

  /**
   * Adds an administrator to a community by linking the administrator's user ID to the
   * community in the community repository and updating the community's admin set. It
   * uses a repository to find the administrator by user ID and then adds the community
   * to their communities.
   *
   * @param community community to which an administrator is being added.
   *
   * @param userId identifier of the user to be added as an administrator to the specified
   * community.
   *
   * @returns the updated `Community` object with the specified user added as an admin.
   */
  private Community addAdminToCommunity(Community community, String userId) {
    communityAdminRepository.findByUserIdWithCommunities(userId).ifPresent(admin -> {
      admin.getCommunities().add(community);
      Set<User> admins = new HashSet<>();
      admins.add(admin);
      community.setAdmins(admins);
    });
    return community;
  }

  /**
   * Returns a set of all communities from the database,
   * utilizing pagination as specified by the `Pageable` object,
   * and ensures uniqueness by using a HashSet.
   *
   * @param pageable pagination criteria for retrieving a subset of data from the database.
   *
   * @returns a set of Community objects retrieved from the database.
   */
  @Override
  public Set<Community> listAll(Pageable pageable) {
    Set<Community> communityListSet = new HashSet<>();
    communityRepository.findAll(pageable).forEach(communityListSet::add);
    return communityListSet;
  }

  /**
   * Retrieves all communities from the repository and returns them as a set. It utilizes
   * the `communityRepository` to fetch all communities and adds them to a set using
   * the `HashSet` data structure. The result is then returned to the caller.
   *
   * @returns a set of community objects retrieved from the community repository.
   */
  @Override public Set<Community> listAll() {
    Set<Community> communities = new HashSet<>();
    communityRepository.findAll().forEach(communities::add);
    return communities;
  }

  /**
   * Retrieves a list of community houses associated with a specified community ID,
   * paginated according to the provided Pageable object. If the community ID exists,
   * it returns the list of community houses; otherwise, it returns an empty Optional.
   *
   * @param communityId identifier of a community for which community houses are being
   * searched.
   *
   * @param pageable pagination criteria for retrieving a page of data from the database.
   *
   * @returns an Optional containing a list of CommunityHouses or an empty Optional if
   * community does not exist.
   */
  @Override
  public Optional<List<CommunityHouse>> findCommunityHousesById(String communityId,
      Pageable pageable) {
    boolean exists = communityRepository.existsByCommunityId(communityId);
    if (exists) {
      return Optional.of(
          communityHouseRepository.findAllByCommunity_CommunityId(communityId, pageable));
    }
    return Optional.empty();
  }

  /**
   * Returns an optional list of community admins for a given community ID. It first
   * checks if the community exists, and if so, retrieves the admins using the community
   * admin repository. If the community does not exist, it returns an empty optional.
   *
   * @param communityId identifier of a community for which to find administrators.
   *
   * @param pageable pagination criteria for retrieving a page of data from the community
   * admin repository.
   *
   * Destructure: `Pageable pageable` can be destructured into `int page`, `int size`,
   * `Sort sort` and `Direction direction`.
   * Main properties: `page` (current page number), `size` (number of items per page),
   * `sort` (sorting criteria), `direction` (sorting order).
   *
   * @returns an Optional containing a list of community admins or an empty Optional.
   *
   * The returned output is of type `Optional<List<User>>`.
   */
  @Override
  public Optional<List<User>> findCommunityAdminsById(String communityId,
      Pageable pageable) {
    boolean exists = communityRepository.existsByCommunityId(communityId);
    if (exists) {
      return Optional.of(
          communityAdminRepository.findAllByCommunities_CommunityId(communityId, pageable)
      );
    }
    return Optional.empty();
  }

  /**
   * Returns an Optional containing a User object if a user with the specified adminId
   * exists in the communityAdminRepository, otherwise it returns an empty Optional.
   * It appears to be a simple lookup function. The result is wrapped in an Optional
   * to handle potential null values.
   *
   * @param adminId unique identifier of the community administrator to be searched.
   *
   * @returns an Optional containing a User object if found, or an empty Optional otherwise.
   */
  @Override
  public Optional<User> findCommunityAdminById(String adminId) {
    return communityAdminRepository.findByUserId(adminId);
  }

  /**
   * Retrieves community details from the database based on a provided community ID.
   * The community ID is used to query the community repository, which returns the
   * corresponding community details as an Optional object.
   *
   * @param communityId identifier of a community for which details are being retrieved.
   *
   * @returns an Optional containing a Community object if found, or an empty Optional
   * if not found.
   */
  @Override public Optional<Community> getCommunityDetailsById(String communityId) {
    return communityRepository.findByCommunityId(communityId);
  }

  /**
   * Retrieves the community details for a given ID along with its associated administrators.
   * It returns an Optional containing the community details if found, otherwise an
   * empty Optional.
   * The community data is retrieved from a community repository.
   *
   * @param communityId identifier used to retrieve community details along with their
   * associated administrators from the repository.
   *
   * @returns an Optional containing a Community object with its associated admins.
   */
  @Override
  public Optional<Community> getCommunityDetailsByIdWithAdmins(String communityId) {
    return communityRepository.findByCommunityIdWithAdmins(communityId);
  }

  /**
   * Adds administrators to a community by updating the community and the administrators'
   * records in the database. It retrieves the community and its administrators, adds
   * the community to the administrators' lists, and saves the updated records.
   *
   * @param communityId identifier of a community to which administrators are to be added.
   *
   * @param adminsIds set of user IDs to be added as admins to the specified community.
   *
   * Contain a set of unique user IDs.
   *
   * @returns an Optional containing a Community object or an empty Optional if the
   * community is not found.
   *
   * The returned output is an `Optional` containing a `Community` object.
   */
  @Override
  public Optional<Community> addAdminsToCommunity(String communityId, Set<String> adminsIds) {
    Optional<Community> communitySearch =
        communityRepository.findByCommunityIdWithAdmins(communityId);

    return communitySearch.map(community -> {
      adminsIds.forEach(adminId -> {
        communityAdminRepository.findByUserIdWithCommunities(adminId).map(admin -> {
          admin.getCommunities().add(community);
          community.getAdmins().add(communityAdminRepository.save(admin));
          return admin;
        });
      });
      return Optional.of(communityRepository.save(community));
    }).orElseGet(Optional::empty);
  }

  /**
   * Adds houses to a community if they do not already exist in the community's collection.
   *
   * @param communityId identifier for the community to which houses are being added.
   *
   * @param houses set of community houses to be added to the specified community.
   *
   * Contain a set of `CommunityHouse` objects.
   *
   * @returns a set of unique IDs of newly added houses to the specified community.
   *
   * The returned output is a `Set` of `String` values. Each `String` value represents
   * a unique `houseId` added to the specified community.
   */
  @Override
  public Set<String> addHousesToCommunity(String communityId, Set<CommunityHouse> houses) {
    Optional<Community> communitySearch =
        communityRepository.findByCommunityIdWithHouses(communityId);

    return communitySearch.map(community -> {
      Set<String> addedIds = new HashSet<>();

      houses.forEach(house -> {
        if (house != null) {
          boolean houseExists = community.getHouses().stream()
              .noneMatch(communityHouse ->
                  communityHouse.getHouseId().equals(house.getHouseId())
                      && communityHouse.getName().equals(house.getName())
              );
          if (houseExists) {
            house.setHouseId(generateUniqueId());
            house.setCommunity(community);
            addedIds.add(house.getHouseId());
            communityHouseRepository.save(house);
            community.getHouses().add(house);
          }
        }
      });

      communityRepository.save(community);

      return addedIds;
    }).orElse(new HashSet<>());
  }

  /**
   * Removes an admin from a community by community ID and admin ID, updates the community
   * repository, and returns true if the admin was successfully removed.
   *
   * @param communityId identifier of the community from which an administrator is to
   * be removed.
   *
   * @param adminId ID of the administrator to be removed from the specified community.
   *
   * @returns a boolean indicating whether the admin was successfully removed from the
   * community.
   */
  @Override
  public boolean removeAdminFromCommunity(String communityId, String adminId) {
    Optional<Community> communitySearch =
        communityRepository.findByCommunityIdWithAdmins(communityId);
    return communitySearch.map(community -> {
      boolean adminRemoved =
          community.getAdmins().removeIf(admin -> admin.getUserId().equals(adminId));
      if (adminRemoved) {
        communityRepository.save(community);
        return true;
      } else {
        return false;
      }
    }).orElse(false);
  }

  /**
   * Deletes a community along with its associated houses from the database. It retrieves
   * the community with the specified ID, removes its houses, and then deletes the
   * community. The operation is transactional to ensure data consistency.
   *
   * @param communityId identifier of the community to be deleted.
   *
   * @returns a boolean value indicating whether the community and its houses were
   * successfully deleted.
   */
  @Override
  @Transactional
  public boolean deleteCommunity(String communityId) {
    return communityRepository.findByCommunityIdWithHouses(communityId)
        .map(community -> {
          Set<String> houseIds = community.getHouses()
              .stream()
              .map(CommunityHouse::getHouseId)
              .collect(Collectors.toSet());

          houseIds.forEach(houseId -> removeHouseFromCommunityByHouseId(community, houseId));
          communityRepository.delete(community);

          return true;
        })
        .orElse(false);
  }

  /**
   * Generates a unique identifier as a string based on random numbers.
   * It uses the UUID (Universally Unique Identifier) class to create a random UUID and
   * converts it to a string representation.
   * The generated string can be used to uniquely identify objects.
   *
   * @returns a random 128-bit UUID string in hexadecimal format.
   */
  private String generateUniqueId() {
    return UUID.randomUUID().toString();
  }

  /**
   * Removes a house from a community by its ID, deletes its associated house members,
   * and saves the updated community.
   *
   * @param community Community object to which a house with the specified `houseId`
   * will be removed.
   *
   * Contain a set of houses and a set of community information.
   *
   * @param houseId identifier of the house to be removed from the community.
   *
   * @returns a boolean indicating whether the house was successfully removed from the
   * community.
   */
  @Transactional
  @Override
  public boolean removeHouseFromCommunityByHouseId(Community community, String houseId) {
    if (community == null) {
      return false;
    } else {
      Optional<CommunityHouse> houseOptional =
          communityHouseRepository.findByHouseIdWithHouseMembers(houseId);
      return houseOptional.map(house -> {
        Set<CommunityHouse> houses = community.getHouses();
        houses.remove(
            house); //remove the house before deleting house members because otherwise the Set relationship would be broken and remove would not work

        Set<String> memberIds = house.getHouseMembers()
            .stream()
            .map(HouseMember::getMemberId)
            .collect(
                Collectors.toSet()); //streams are immutable so need to collect all the member IDs and then delete them from the house

        memberIds.forEach(id -> houseService.deleteMemberFromHouse(houseId, id));

        communityRepository.save(community);
        communityHouseRepository.deleteByHouseId(houseId);
        return true;
      }).orElse(false);
    }
  }
}
