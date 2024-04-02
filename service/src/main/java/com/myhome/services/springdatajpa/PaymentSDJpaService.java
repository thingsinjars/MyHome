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

import com.myhome.controllers.dto.PaymentDto;
import com.myhome.controllers.dto.mapper.PaymentMapper;
import com.myhome.domain.HouseMember;
import com.myhome.domain.Payment;
import com.myhome.domain.User;
import com.myhome.repositories.HouseMemberRepository;
import com.myhome.repositories.PaymentRepository;
import com.myhome.repositories.UserRepository;
import com.myhome.services.PaymentService;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implements {@link PaymentService} and uses Spring Data JPA Repository to do its work
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentSDJpaService implements PaymentService {
  private final PaymentRepository paymentRepository;
  private final UserRepository adminRepository;
  private final PaymentMapper paymentMapper;
  private final HouseMemberRepository houseMemberRepository;

  /**
   * generates a payment ID and creates a new payment record in the repository.
   * 
   * @param request payment details and is used to generate a unique payment ID and
   * create a new payment record in the repository.
   * 
   * @returns a payment DTO containing the generated payment ID and created payment details.
   */
  @Override
  public PaymentDto schedulePayment(PaymentDto request) {
    generatePaymentId(request);
    return createPaymentInRepository(request);
  }

  /**
   * retrieves a payment's details from the repository, maps it to a `PaymentDto` object
   * using the `paymentMapper`, and returns the result as an optional object.
   * 
   * @param paymentId unique identifier of a payment and is used to retrieve the
   * corresponding payment details from the repository.
   * 
   * @returns an Optional<PaymentDto> containing the payment details for the provided
   * payment ID.
   */
  @Override
  public Optional<PaymentDto> getPaymentDetails(String paymentId) {
    return paymentRepository.findByPaymentId(paymentId)
        .map(paymentMapper::paymentToPaymentDto);
  }

  /**
   * retrieves a specific `HouseMember` instance from the repository based on its `memberId`.
   * 
   * @param memberId identifier of the House Member to be retrieved.
   * 
   * @returns an optional instance of `HouseMember`.
   */
  @Override
  public Optional<HouseMember> getHouseMember(String memberId) {
    return houseMemberRepository.findByMemberId(memberId);
  }

  /**
   * retrieves a set of payments associated with a given member ID from the payment repository.
   * 
   * @param memberId member ID of the payments to be retrieved.
   * 
   * @returns a set of payments belonging to the specified member.
   */
  @Override
  public Set<Payment> getPaymentsByMember(String memberId) {
    ExampleMatcher ignoringMatcher = ExampleMatcher.matchingAll()
        .withMatcher("memberId",
            ExampleMatcher.GenericPropertyMatchers.startsWith().ignoreCase())
        .withIgnorePaths("paymentId", "charge", "type", "description", "recurring", "dueDate",
            "admin");

    Example<Payment> paymentExample =
        Example.of(new Payment(null, null, null, null, false, null, null,
                new HouseMember().withMemberId(memberId)),
            ignoringMatcher);

    return new HashSet<>(paymentRepository.findAll(paymentExample));
  }

  /**
   * retrieves all payments for a given administrator (identified by their `adminId`)
   * from the database using Pageable.
   * 
   * @param adminId ID of the admin for whom the payments are being retrieved.
   * 
   * @param pageable pagination information for the query, allowing the method to
   * retrieve a specific page of results from the database.
   * 
   * @returns a paginated list of payments for the specified administrator.
   */
  @Override
  public Page<Payment> getPaymentsByAdmin(String adminId, Pageable pageable) {
    ExampleMatcher ignoringMatcher = ExampleMatcher.matchingAll()
        .withMatcher("adminId",
            ExampleMatcher.GenericPropertyMatchers.startsWith().ignoreCase())
        .withIgnorePaths("paymentId", "charge", "type", "description", "recurring", "dueDate",
            "memberId");

    Example<Payment> paymentExample =
        Example.of(
            new Payment(null, null, null, null, false, null, new User().withUserId(adminId), null),
            ignoringMatcher);

    return paymentRepository.findAll(paymentExample, pageable);
  }

  /**
   * maps a `PaymentDto` object to a `Payment` object, saves the admin and payment
   * entities in their respective repositories, and maps the created `Payment` back to
   * a `PaymentDto`.
   * 
   * @param request PaymentDto object containing information about the payment to be created.
   * 
   * @returns a `PaymentDto` object representing the saved payment data.
   */
  private PaymentDto createPaymentInRepository(PaymentDto request) {
    Payment payment = paymentMapper.paymentDtoToPayment(request);

    adminRepository.save(payment.getAdmin());
    paymentRepository.save(payment);

    return paymentMapper.paymentToPaymentDto(payment);
  }

  /**
   * generates a unique payment ID for a given `PaymentDto` request using `UUID.randomUUID()`.
   * 
   * @param request PaymentDto object that contains the necessary information for
   * generating a payment ID, which is then set to a unique UUID string returned by the
   * `UUID.randomUUID()` method.
   */
  private void generatePaymentId(PaymentDto request) {
    request.setPaymentId(UUID.randomUUID().toString());
  }
}






















