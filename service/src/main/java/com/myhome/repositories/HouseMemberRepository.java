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

package com.myhome.repositories;

import com.myhome.domain.HouseMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * Extends CrudRepository to provide custom repository methods for retrieving HouseMember
 * data based on various criteria.
 */
public interface HouseMemberRepository extends CrudRepository<HouseMember, Long> {
  Optional<HouseMember> findByMemberId(String memberId);

  List<HouseMember> findAllByCommunityHouse_HouseId(String houseId, Pageable pageable);

  List<HouseMember> findAllByCommunityHouse_Community_Admins_UserId(String userId,
      Pageable pageable);
}
