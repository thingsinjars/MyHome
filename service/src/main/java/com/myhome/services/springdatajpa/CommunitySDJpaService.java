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
   * generates a unique ID for a new community, adds an administrator with the provided
   * user ID to the community, saves it to the repository, and returns the saved community
   * object.
   * 
   * @param communityDto Community data that needs to be created or updated, and it
   * contains necessary information such as name, description, etc.
   * 
   * @returns a `Community` object representing the created community with its ID.
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
   * updates a community's list of admins by adding an admin user to it based on their
   * user ID, and then sets the updated admin list as the community's admins.
   * 
   * @param community Community object that the function is acting upon, and its state
   * is modified by adding or removing an administrator.
   * 
   * @param userId identifier of the user who is being added as an admin to the specified
   * Community.
   * 
   * @returns a reference to the updated `Community` object with the added admin user.
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
   * list all communities in a repository.
   * 
   * @param pageable page number and the page size to be retrieved from the community
   * repository, allowing for efficient retrieval of a subset of the communities in the
   * database.
   * 
   * @returns a set of all communities found in the repository.
   */
  @Override
  public Set<Community> listAll(Pageable pageable) {
    Set<Community> communityListSet = new HashSet<>();
    communityRepository.findAll(pageable).forEach(communityListSet::add);
    return communityListSet;
  }

  /**
   * retrieves all communities from the database using the `communityRepository.findAll()`
   * method and stores them in a `Set` for later use.
   * 
   * @returns a set of all available communities.
   */
  @Override public Set<Community> listAll() {
    Set<Community> communities = new HashSet<>();
    communityRepository.findAll().forEach(communities::add);
    return communities;
  }

  /**
   * retrieves a list of `CommunityHouse` objects belonging to a specific `communityId`.
   * If the community exists, it returns an `Optional` containing the list of houses.
   * Otherwise, it returns an empty `Optional`.
   * 
   * @param communityId identifier of the community whose houses are to be retrieved.
   * 
   * @param pageable page number and the number of items per page that the user wants
   * to retrieve from the database.
   * 
   * @returns a `Optional` of `List<CommunityHouse>` objects, which contains the community
   * houses associated with the given `communityId`.
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
   * retrieves a list of community admins for a given community ID, using a pageable
   * interface to handle pagination.
   * 
   * @param communityId unique identifier of the community whose admin users should be
   * retrieved.
   * 
   * @param pageable page number and limit of the result set that the method will return,
   * allowing for efficient pagination and retrieval of a subset of the data in the database.
   * 
   * @returns a `Optional` object containing a list of `User` objects associated with
   * the specified community ID.
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
   * retrieves a `Optional<User>` object representing the community administrator
   * associated with the given `adminId`.
   * 
   * @param adminId ID of the community administrator to be retrieved.
   * 
   * @returns an optional `User` object representing the community administrator with
   * the specified ID.
   */
  @Override
  public Optional<User> findCommunityAdminById(String adminId) {
    return communityAdminRepository.findByUserId(adminId);
  }

  /**
   * retrieves community details by Id.
   * 
   * @param communityId unique identifier of a community for which details are sought.
   * 
   * @returns an optional instance of `Community`.
   */
  @Override public Optional<Community> getCommunityDetailsById(String communityId) {
    return communityRepository.findByCommunityId(communityId);
  }

  /**
   * retrieves community details and admins associated with a given community ID from
   * the repository.
   * 
   * @param communityId identifier of the Community for which details are being requested,
   * and it is used to retrieve the Community record from the repository along with its
   * associated admins.
   * 
   * @returns an Optional containing the details of the specified community and its administrators.
   */
  @Override
  public Optional<Community> getCommunityDetailsByIdWithAdmins(String communityId) {
    return communityRepository.findByCommunityIdWithAdmins(communityId);
  }

  /**
   * adds a list of users to a community by finding the community, adding the users as
   * admins, and saving the updated community.
   * 
   * @param communityId ID of the community for which the admins should be added.
   * 
   * @param adminsIds set of user IDs of the admins to add to the community.
   * 
   * @returns an `Optional<Community>` containing the updated community with added admins.
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
   * adds new or updated houses to a community by searching for the community, updating
   * or creating houses, and saving them in the database while also adding them to the
   * community's house list.
   * 
   * @param communityId identifier of the community to which the houses will be added,
   * and is used to retrieve the existing houses in the community and to identify the
   * new houses to be added.
   * 
   * @param houses sets of houses to be added to the community, which are then added
   * or generated new unique id and saved in the database.
   * 
   * @returns a set of house IDs that have been newly created and added to the community.
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
   * removes an admin from a community by finding the community and removing the admin
   * using the `getAdmins()` method, then saving the updated community to the repository.
   * 
   * @param communityId unique identifier of a community for which an admin is to be removed.
   * 
   * @param adminId ID of an administrator to be removed from a community.
   * 
   * @returns a boolean value indicating whether an admin was successfully removed from
   * a community.
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
   * deletes a community by first finding all houses associated with it, then deleting
   * those houses and finally deleting the community.
   * 
   * @param communityId identifier of the community to be deleted.
   * 
   * @returns a boolean value indicating whether the community was successfully deleted.
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
   * generates a unique identifier using the `UUID` class and returns it as a string.
   * 
   * @returns a unique string of 36 characters, consisting of numbers and letters.
   */
  private String generateUniqueId() {
    return UUID.randomUUID().toString();
  }

  /**
   * removes a house from a community by first removing it from the community's set of
   * houses, then deleting the house members associated with the house, and finally
   * saving the community and deleting the house.
   * 
   * @param community Community object that contains the houses to be removed, and is
   * used to delete the corresponding houses from the Community objects' houses set.
   * 
   * @param houseId identifier of the house to be removed from the community, which is
   * used as a reference to locate and remove the house and its associated members from
   * the community's houses and member lists.
   * 
   * @returns a boolean value indicating whether the house was successfully removed
   * from the community.
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
























