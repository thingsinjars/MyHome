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

package com.myhome.controllers;

import com.myhome.api.PaymentsApi;
import com.myhome.controllers.dto.PaymentDto;
import com.myhome.controllers.mapper.SchedulePaymentApiMapper;
import com.myhome.controllers.request.EnrichedSchedulePaymentRequest;
import com.myhome.domain.Community;
import com.myhome.domain.CommunityHouse;
import com.myhome.domain.HouseMember;
import com.myhome.domain.Payment;
import com.myhome.domain.User;
import com.myhome.model.AdminPayment;
import com.myhome.model.ListAdminPaymentsResponse;
import com.myhome.model.ListMemberPaymentsResponse;
import com.myhome.model.SchedulePaymentRequest;
import com.myhome.model.SchedulePaymentResponse;
import com.myhome.services.CommunityService;
import com.myhome.services.PaymentService;
import com.myhome.utils.PageInfo;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller which provides endpoints for managing payments
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController implements PaymentsApi {
  private final PaymentService paymentService;
  private final CommunityService communityService;
  private final SchedulePaymentApiMapper schedulePaymentApiMapper;

  /**
   * Handles a schedule payment request by verifying user and admin identities, processing
   * the payment, and returning a response based on the result.
   *
   * @param request SchedulePaymentRequest object containing the schedule payment details.
   *
   * Contain a member ID and an admin ID.
   *
   * @returns either a `SchedulePaymentResponse` in a `201 Created` status or a
   * `ResponseEntity` indicating not found.
   *
   * The returned `ResponseEntity` contains a `SchedulePaymentResponse` object as its
   * body. The `SchedulePaymentResponse` object contains the details of the scheduled
   * payment.
   */
  @Override
  public ResponseEntity<SchedulePaymentResponse> schedulePayment(@Valid
      SchedulePaymentRequest request) {
    log.trace("Received schedule payment request");

    HouseMember houseMember = paymentService.getHouseMember(request.getMemberId())
        .orElseThrow(() -> new RuntimeException(
            "House member with given id not exists: " + request.getMemberId()));
    User admin = communityService.findCommunityAdminById(request.getAdminId())
        .orElseThrow(
            () -> new RuntimeException("Admin with given id not exists: " + request.getAdminId()));

    if (isUserAdminOfCommunityHouse(houseMember.getCommunityHouse(), admin)) {
      final EnrichedSchedulePaymentRequest paymentRequest =
          schedulePaymentApiMapper.enrichSchedulePaymentRequest(request, admin, houseMember);
      final PaymentDto paymentDto =
          schedulePaymentApiMapper.enrichedSchedulePaymentRequestToPaymentDto(paymentRequest);
      final PaymentDto processedPayment = paymentService.schedulePayment(paymentDto);
      final SchedulePaymentResponse paymentResponse =
          schedulePaymentApiMapper.paymentToSchedulePaymentResponse(processedPayment);
      return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponse);
    }

    return ResponseEntity.notFound().build();
  }

  /**
   * Determines whether a specified user is an admin of a given community house by
   * checking if the user is in the list of admins for that community house.
   *
   * @param communityHouse community house being queried for the presence of a specific
   * administrator.
   *
   * @param admin user to be checked for admin rights in the specified community house.
   *
   * @returns a boolean indicating whether the specified admin is an admin of the
   * community house.
   */
  private boolean isUserAdminOfCommunityHouse(CommunityHouse communityHouse, User admin) {
    return communityHouse.getCommunity()
        .getAdmins()
        .contains(admin);
  }

  /**
   * Handles a request to retrieve payment details by ID, returns a `ResponseEntity`
   * containing the payment details in a `SchedulePaymentResponse` format if found,
   * otherwise returns a 404 response.
   *
   * @param paymentId identifier of a payment for which details are to be retrieved.
   *
   * @returns a ResponseEntity containing a SchedulePaymentResponse if found, or a
   * ResponseEntity with a 404 status code if not.
   */
  @Override
  public ResponseEntity<SchedulePaymentResponse> listPaymentDetails(String paymentId) {
    log.trace("Received request to get details about a payment with id[{}]", paymentId);

    return paymentService.getPaymentDetails(paymentId)
        .map(schedulePaymentApiMapper::paymentToSchedulePaymentResponse)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Handles a request to retrieve all payments for a house member with the given ID.
   * It maps the member and payments data into a REST API response. Returns a 404
   * response if the member is not found.
   *
   * @param memberId identifier of the house member for whom all payments are to be retrieved.
   *
   * @returns a ResponseEntity containing a ListMemberPaymentsResponse object or a 404
   * response if not found.
   *
   * The output is a `ResponseEntity` object, which contains an HTTP response.
   */
  @Override
  public ResponseEntity<ListMemberPaymentsResponse> listAllMemberPayments(String memberId) {
    log.trace("Received request to list all the payments for the house member with id[{}]",
        memberId);

    return paymentService.getHouseMember(memberId)
        .map(payments -> paymentService.getPaymentsByMember(memberId))
        .map(schedulePaymentApiMapper::memberPaymentSetToRestApiResponseMemberPaymentSet)
        .map(memberPayments -> new ListMemberPaymentsResponse().payments(memberPayments))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Retrieves a list of scheduled payments made by a specific admin within a given
   * community, and returns the list as a response entity. If the admin is not found
   * in the community, it returns a 404 not found response.
   *
   * @param communityId identifier of a community, with which the function determines
   * if the provided admin ID is associated.
   *
   * @param adminId identifier of the admin whose scheduled payments are to be retrieved.
   *
   * @param pageable pagination criteria for retrieving a subset of data from the payment
   * service.
   *
   * Destructure:
   * - `pageable` is a Pageable object.
   * - It has properties: `sort`, `pageNumber`, `pageSize`, `offset`, `pageNumber`,
   * `pageSize`, `sort`, `paged`, `unpaged`, `sort`, `direction`, `empty`, `size`,
   * `number`, `sort`, `paged`, `unpaged`, `sort`, `direction`, `empty`, `size`, `number`,
   * `sort`, `paged`, `unpaged`, `sort`, `direction`, `empty`, `size`, `number`.
   *
   * Main properties are:
   * - `pageNumber` and `pageSize` for pagination.
   *
   * @returns a `ResponseEntity` containing a list of `AdminPayment` objects or a 404
   * response.
   *
   * The returned `ResponseEntity` contains a `ListAdminPaymentsResponse` object, which
   * has `adminPayments` and `pageInfo` attributes. The `adminPayments` attribute is a
   * set of `AdminPayment` objects, and the `pageInfo` attribute is a `PageInfo` object.
   */
  @Override
  public ResponseEntity<ListAdminPaymentsResponse> listAllAdminScheduledPayments(
      String communityId, String adminId, Pageable pageable) {
    log.trace("Received request to list all the payments scheduled by the admin with id[{}]",
        adminId);

    final boolean isAdminInGivenCommunity = isAdminInGivenCommunity(communityId, adminId);

    if (isAdminInGivenCommunity) {
      final Page<Payment> paymentsForAdmin = paymentService.getPaymentsByAdmin(adminId, pageable);
      final List<Payment> payments = paymentsForAdmin.getContent();
      final Set<AdminPayment> adminPayments =
          schedulePaymentApiMapper.adminPaymentSetToRestApiResponseAdminPaymentSet(
              new HashSet<>(payments));
      final ListAdminPaymentsResponse response = new ListAdminPaymentsResponse()
          .payments(adminPayments)
          .pageInfo(PageInfo.of(pageable, paymentsForAdmin));
      return ResponseEntity.ok().body(response);
    }

    return ResponseEntity.notFound().build();
  }

  /**
   * Checks if a given admin ID exists within a specified community ID by retrieving
   * community details and verifying admin membership. It throws a runtime exception
   * if the community does not exist.
   *
   * @param communityId identifier for the community whose details, including administrators,
   * are being retrieved from the `communityService`.
   *
   * @param adminId identifier of the administrator to be checked for membership in the
   * specified community.
   *
   * @returns a boolean indicating whether the admin with the given ID is in the specified
   * community.
   */
  private Boolean isAdminInGivenCommunity(String communityId, String adminId) {
    return communityService.getCommunityDetailsByIdWithAdmins(communityId)
        .map(Community::getAdmins)
        .map(admins -> admins.stream().anyMatch(admin -> admin.getUserId().equals(adminId)))
        .orElseThrow(
            () -> new RuntimeException("Community with given id not exists: " + communityId));
  }
}
