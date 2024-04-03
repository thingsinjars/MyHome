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

@Mapper
public interface SchedulePaymentApiMapper {

  /**
   * builds a `UserDto` object representing an admin user with the provided `adminId`.
   * 
   * @param adminId user ID of an administrator in the `UserDto` object constructed by
   * the function.
   * 
   * @returns a `UserDto` object representing the admin with the provided ID.
   */
  @Named("adminIdToAdmin")
  static UserDto adminIdToAdminDto(String adminId) {
    return UserDto.builder()
        .userId(adminId)
        .build();
  }

  /**
   * maps a `memberId` string to an instance of `HouseMemberDto`.
   * 
   * @param memberId unique identifier of a member in the `HouseMemberDto`.
   * 
   * @returns a `HouseMemberDto` object containing the specified member ID.
   */
  @Named("memberIdToMember")
  static HouseMemberDto memberIdToMemberDto(String memberId) {
    return new HouseMemberDto()
        .memberId(memberId);
  }

  /**
   * maps a `UserDto` object to its corresponding `String` representation, which is the
   * user ID.
   * 
   * @param userDto User object in the function, providing its `UserId` property as the
   * output.
   * 
   * @returns a `String` representing the user ID of the input `UserDto`.
   */
  @Named("adminToAdminId")
  static String adminToAdminId(UserDto userDto) {
    return userDto.getUserId();
  }

  /**
   * maps a `HouseMemberDto` object to its corresponding `MemberId`.
   * 
   * @param houseMemberDto House Member object containing information about a member
   * of a house, which is passed to the function as a reference for the purpose of
   * retrieving the member's ID.
   * 
   * @returns a string representing the member ID of the inputted House Member Dto.
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
   * maps the user details from the `enrichedSchedulePaymentRequest` to both the member
   * and admin fields of the `paymentDto`, utilizing the `@Builder` annotation to obtain
   * the `Builder` instance.
   * 
   * @param paymentDto Payment DTO object that will be modified based on the user details
   * of the given `enrichedSchedulePaymentRequest`.
   * 
   * 		- `PaymentDto`: This is an annotated class that has a builder annotation `@Builder`.
   * The `PaymentDto` class has several attributes, including `member`, `admin`, and `enrichedSchedulePaymentRequest`.
   * 		- `getEnrichedRequestMember()`: This function returns the enriched member details
   * of the payment request. It is not provided in the input `enrichedSchedulePaymentRequest`.
   * 		- `getEnrichedRequestAdmin()`: This function returns the enriched administrator
   * details of the payment request. It is not provided in the input `enrichedSchedulePaymentRequest`.
   * 
   * 
   * @param enrichedSchedulePaymentRequest PaymentDto object that has been enriched
   * with additional data, which is then used to map the user details of the payment
   * request to an administrator and a house member.
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
   * enriches a `SchedulePaymentRequest` object with additional information from the
   * administrator and member, including community IDs, admin and member details, and
   * extra fields for recurring payments.
   * 
   * @param request SchedulePaymentRequest object that contains the details of the
   * payment request to be enriched.
   * 
   * @param admin user who will be associated with the payment request, providing their
   * ID, name, email address, and encrypted password for further processing.
   * 
   * @param member HouseMember object that is associated with the Schedule Payment
   * Request, providing its ID, name, and community house information to be included
   * in the enriched payment request.
   * 
   * @returns an enriched Schedule Payment Request object.
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
   * generates a `UserDto` object containing information about an administrator associated
   * with a scheduled payment request, including their user ID, entity ID, name, email,
   * encrypted password, and builds the object.
   * 
   * @param enrichedSchedulePaymentRequest admin entity for which the user DTO is to
   * be built, providing the admin ID, name, email, encrypted password, and other
   * relevant details.
   * 
   * @returns a `UserDto` object with enriched administrator details.
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
   * creates a `HouseMemberDto` object from an `EnrichedSchedulePaymentRequest` by
   * populating its fields with values from the request, including the member entity
   * ID and name.
   * 
   * @param enrichedSchedulePaymentRequest payload that is being enriched and transformed
   * into a new object, which is then returned by the `getEnrichedRequestMember()` function.
   * 
   * @returns a HouseMemberDto object containing member ID, name, and entity ID.
   */
  default HouseMemberDto getEnrichedRequestMember(EnrichedSchedulePaymentRequest enrichedSchedulePaymentRequest) {
    return new HouseMemberDto()
        .id(enrichedSchedulePaymentRequest.getMemberEntityId())
        .memberId(enrichedSchedulePaymentRequest.getMemberId())
        .name(enrichedSchedulePaymentRequest.getHouseMemberName());
  }
}
























