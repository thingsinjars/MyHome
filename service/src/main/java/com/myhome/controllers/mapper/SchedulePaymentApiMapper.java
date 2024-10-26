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

package com.myhome.controllers.mapper;

import com.myhome.controllers.dto.PaymentDto;
import com.myhome.controllers.dto.UserDto;
import com.myhome.controllers.request.EnrichedSchedulePaymentRequest;
import com.myhome.domain.Community;
import com.myhome.domain.HouseMember;
import com.myhome.domain.Payment;
import com.myhome.domain.User;
import com.myhome.model.AdminPayment;
import com.myhome.model.HouseMemberDto;
import com.myhome.model.MemberPayment;
import com.myhome.model.SchedulePaymentRequest;
import com.myhome.model.SchedulePaymentResponse;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

/**
 * Provides a set of mappings between different data transfer objects and domain objects.
 */
@Mapper
public interface SchedulePaymentApiMapper {

  /**
   * Maps an admin ID to a UserDto object, creating a new UserDto with the given admin
   * ID as the user ID. The function uses the UserDto builder to construct the object.
   * The resulting UserDto object is returned.
   *
   * @param adminId identifier for the admin user, which is used to construct the
   * `UserDto` object.
   *
   * @returns a UserDto object with the provided adminId as its userId property.
   */
  @Named("adminIdToAdmin")
  static UserDto adminIdToAdminDto(String adminId) {
    return UserDto.builder()
        .userId(adminId)
        .build();
  }

  /**
   * Takes a `memberId` as input, creates a new `HouseMemberDto` object, and sets its
   * `memberId` property to the input value, returning the resulting `HouseMemberDto`
   * instance.
   *
   * @param memberId identifier for a house member that is set in the resulting
   * `HouseMemberDto` object.
   *
   * @returns a HouseMemberDto object with a populated memberId field.
   */
  @Named("memberIdToMember")
  static HouseMemberDto memberIdToMemberDto(String memberId) {
    return new HouseMemberDto()
        .memberId(memberId);
  }

  /**
   * Extracts the user ID from a `UserDto` object, returning it as a string. The function
   * is annotated with `@Named`, indicating it is a named method. It appears to be used
   * to retrieve a unique identifier for an administrative user.
   *
   * @param userDto data transfer object containing user information.
   *
   * @returns the user ID of the given `UserDto` object.
   */
  @Named("adminToAdminId")
  static String adminToAdminId(UserDto userDto) {
    return userDto.getUserId();
  }

  /**
   * Extracts the `memberId` from a `HouseMemberDto` object and returns it as a string.
   * It is annotated with `@Named` to assign it a specific name. The function takes a
   * `HouseMemberDto` object as input and returns a string value.
   *
   * @param houseMemberDto data transfer object containing member information to be
   * processed by the function.
   *
   * @returns a string representing the ID of the house member.
   */
  @Named("memberToMemberId")
  static String memberToMemberId(HouseMemberDto houseMemberDto) {
    return houseMemberDto.getMemberId();
  }

  @Mappings({
      @Mapping(source = "adminId", target = "admin", qualifiedByName = "adminIdToAdmin"),
      @Mapping(source = "memberId", target = "member", qualifiedByName = "memberIdToMember")
  })
  PaymentDto schedulePaymentRequestToPaymentDto(SchedulePaymentRequest schedulePaymentRequest);

  PaymentDto enrichedSchedulePaymentRequestToPaymentDto(
      EnrichedSchedulePaymentRequest enrichedSchedulePaymentRequest);

  /**
   * Maps user details from an `EnrichedSchedulePaymentRequest` to a `PaymentDto`
   * instance, converting user details to admin and house member fields using the
   * `getEnrichedRequestAdmin` and `getEnrichedRequestMember` methods.
   *
   * @param paymentDto builder instance of the `PaymentDto` class, which is annotated
   * with `@Builder` and is used to configure the object's properties.
   *
   * @param enrichedSchedulePaymentRequest object containing the enriched user details
   * of the payment request.
   */
  @AfterMapping
  default void setUserFields(@MappingTarget PaymentDto.PaymentDtoBuilder paymentDto, EnrichedSchedulePaymentRequest enrichedSchedulePaymentRequest) {
    // MapStruct and Lombok requires you to pass in the Builder instance of the class if that class is annotated with @Builder, or else the AfterMapping method is not used.
    // required to use AfterMapping to convert the user details of the payment request to admin, and same with house member
    paymentDto.member(getEnrichedRequestMember(enrichedSchedulePaymentRequest));
    paymentDto.admin(getEnrichedRequestAdmin(enrichedSchedulePaymentRequest));
  }

  Set<MemberPayment> memberPaymentSetToRestApiResponseMemberPaymentSet(
      Set<Payment> memberPaymentSet);

  @Mapping(target = "memberId", expression = "java(payment.getMember().getMemberId())")
  MemberPayment paymentToMemberPayment(Payment payment);

  Set<AdminPayment> adminPaymentSetToRestApiResponseAdminPaymentSet(
      Set<Payment> memberPaymentSet);

  @Mapping(target = "adminId", expression = "java(payment.getAdmin().getUserId())")
  AdminPayment paymentToAdminPayment(Payment payment);

  @Mappings({
      @Mapping(source = "admin", target = "adminId", qualifiedByName = "adminToAdminId"),
      @Mapping(source = "member", target = "memberId", qualifiedByName = "memberToMemberId")
  })
  SchedulePaymentResponse paymentToSchedulePaymentResponse(PaymentDto payment);

  /**
   * Enriches a `SchedulePaymentRequest` object by adding additional information from
   * the `admin` and `member` objects, including community IDs, admin details, and
   * member details, and returns an `EnrichedSchedulePaymentRequest` object.
   *
   * @param request SchedulePaymentRequest object that is enriched with additional
   * information from the `admin` and `member` objects.
   *
   * Extract the properties of `request`: type, description, recurring flag, charge,
   * due date, and admin ID.
   *
   * @param admin user who is an administrator and is used to obtain community IDs and
   * additional information for enrichment of the request.
   *
   * Exposes its user ID, name, email, and encrypted password.
   *
   * @param member HouseMember associated with the payment request, and its properties
   * are used to enrich the EnrichedSchedulePaymentRequest object.
   *
   * Existed: member.getMemberId(),
   * Exist: member.getId(), member.getName(),
   * Optional: member.getHouseMemberDocument().getDocumentFilename(),
   * Optional: member.getCommunityHouse().getHouseId().
   *
   * @returns an EnrichedSchedulePaymentRequest object with enriched data from admin
   * and member entities.
   *
   * The output is of type `EnrichedSchedulePaymentRequest`. It contains the following
   * properties:
   * - `type`: The type of the schedule payment request.
   * - `description`: A description of the request.
   * - `recurring`: A flag indicating whether the request is recurring.
   * - `charge`: The charge associated with the request.
   * - `dueDate`: The due date of the request.
   * - `adminId`: The ID of the admin.
   * - `adminName`: The name of the admin.
   * - `adminEmail`: The email of the admin.
   * - `adminEncryptedPassword`: The encrypted password of the admin.
   * - `communityIds`: A set of community IDs.
   * - `memberId`: The ID of the member.
   * - `memberId`: The ID of the member.
   * - `memberDocumentFilename`: The filename of the member's document.
   * - `memberName`: The name of the member.
   * - `communityHouseId`: The ID of the community house.
   */
  default EnrichedSchedulePaymentRequest enrichSchedulePaymentRequest(
      SchedulePaymentRequest request, User admin, HouseMember member) {
    Set<String> communityIds = admin.getCommunities()
        .stream()
        .map(Community::getCommunityId)
        .collect(Collectors.toSet());
    return new EnrichedSchedulePaymentRequest(request.getType(),
        request.getDescription(),
        request.isRecurring(),
        request.getCharge(),
        request.getDueDate(),
        request.getAdminId(),
        admin.getId(),
        admin.getName(),
        admin.getEmail(),
        admin.getEncryptedPassword(),
        communityIds,
        member.getMemberId(),
        member.getId(),
        member.getHouseMemberDocument() != null ? member.getHouseMemberDocument()
            .getDocumentFilename() : "",
        member.getName(),
        member.getCommunityHouse() != null ? member.getCommunityHouse().getHouseId() : "");
  }

  /**
   * Constructs a `UserDto` object from the provided `EnrichedSchedulePaymentRequest`
   * object, extracting and mapping its properties to the corresponding fields of the
   * `UserDto` builder.
   *
   * @param enrichedSchedulePaymentRequest source of data for creating a `UserDto`
   * object, providing the required fields for its construction.
   *
   * @returns a `UserDto` object containing admin user details.
   */
  default UserDto getEnrichedRequestAdmin(EnrichedSchedulePaymentRequest enrichedSchedulePaymentRequest) {
    return UserDto.builder()
        .userId(enrichedSchedulePaymentRequest.getAdminId())
        .id(enrichedSchedulePaymentRequest.getAdminEntityId())
        .name(enrichedSchedulePaymentRequest.getAdminName())
        .email(enrichedSchedulePaymentRequest.getAdminEmail())
        .encryptedPassword(enrichedSchedulePaymentRequest.getAdminEncryptedPassword())
        .build();
  }

  /**
   * Extracts and enriches a HouseMemberDto object from an EnrichedSchedulePaymentRequest
   * object, mapping relevant fields such as id, memberId, and name.
   *
   * @param enrichedSchedulePaymentRequest enriched schedule payment request data that
   * is being used to populate the `HouseMemberDto` object.
   *
   * @returns a HouseMemberDto object with id, memberId, and name populated from the
   * enriched request.
   */
  default HouseMemberDto getEnrichedRequestMember(EnrichedSchedulePaymentRequest enrichedSchedulePaymentRequest) {
    return new HouseMemberDto()
        .id(enrichedSchedulePaymentRequest.getMemberEntityId())
        .memberId(enrichedSchedulePaymentRequest.getMemberId())
        .name(enrichedSchedulePaymentRequest.getHouseMemberName());
  }
}
