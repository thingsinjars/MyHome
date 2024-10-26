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
   * Generates a payment ID for a given payment request,
   * then creates the payment in a repository,
   * returning the payment as a PaymentDto object.
   *
   * @param request payment data to be scheduled, which is used to generate a payment
   * ID and create a payment in the repository.
   *
   * @returns a `PaymentDto` object after creating a payment in the repository.
   */
  @Override
  public PaymentDto schedulePayment(PaymentDto request) {
    generatePaymentId(request);
    return createPaymentInRepository(request);
  }

  /**
   * Retrieves payment details from the database and converts them to a PaymentDto
   * object. It takes a payment ID as input, uses a repository to find the corresponding
   * payment, and maps the payment to a PaymentDto using a payment mapper.
   *
   * @param paymentId identifier used to retrieve payment details from the repository.
   *
   * @returns an Optional containing a PaymentDto object if found, or an empty Optional
   * if not.
   */
  @Override
  public Optional<PaymentDto> getPaymentDetails(String paymentId) {
    return paymentRepository.findByPaymentId(paymentId)
        .map(paymentMapper::paymentToPaymentDto);
  }

  /**
   * Retrieves an optional HouseMember object from the repository based on the provided
   * memberId.
   * It uses the `findByMemberId` method of the houseMemberRepository to perform the retrieval.
   * The result is returned as an Optional, allowing for null or empty results.
   *
   * @param memberId identifier for the HouseMember being retrieved.
   *
   * @returns an Optional containing a HouseMember object if found, or an empty Optional
   * otherwise.
   */
  @Override
  public Optional<HouseMember> getHouseMember(String memberId) {
    return houseMemberRepository.findByMemberId(memberId);
  }

  /**
   * Returns a set of payments for a specified member based on a partial match of the
   * member's ID, ignoring certain properties and paths.
   *
   * @param memberId identifier of a member for which payments are being retrieved from
   * the database.
   *
   * @returns a set of Payment objects for a specified member.
   *
   * The returned output is a Set of Payment objects, each containing several attributes,
   * including a unique identifier, payment identifier, charge, type, description,
   * recurring status, due date, and related information about the member.
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
   * Returns a paginated list of payments made by a specified admin, ignoring certain
   * fields and using a case-insensitive search for the admin's ID.
   *
   * @param adminId identifier of the admin whose payments are to be retrieved.
   *
   * @param pageable pagination criteria for the query, enabling the retrieval of a
   * specified page of data from the database.
   *
   * Extracts the `sort`, `page`, and `size` properties from `pageable`.
   * The `sort` property is a `Sort` object that defines the sorting criteria.
   * The `page` property is an `int` that represents the current page number.
   * The `size` property is an `int` that represents the number of items per page.
   *
   * @returns a pageable list of payments made by a specified admin, filtered by certain
   * attributes.
   *
   * The output is a `Page` object, containing a list of `Payment` objects.
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
   * Maps a `PaymentDto` to a `Payment` object,
   * saves an `Admin` and a `Payment` to their respective repositories, and
   * returns the mapped `PaymentDto` object.
   *
   * @param request payment data to be created in the repository, which is mapped to a
   * `Payment` object.
   *
   * @returns a `PaymentDto` object, mapped from the saved `Payment` entity in the database.
   */
  private PaymentDto createPaymentInRepository(PaymentDto request) {
    Payment payment = paymentMapper.paymentDtoToPayment(request);

    adminRepository.save(payment.getAdmin());
    paymentRepository.save(payment);

    return paymentMapper.paymentToPaymentDto(payment);
  }

  /**
   * Assigns a unique payment ID to a PaymentDto object, generated by converting a
   * random UUID to a string.
   *
   * @param request object that contains payment data, which is used to generate a
   * payment ID.
   */
  private void generatePaymentId(PaymentDto request) {
    request.setPaymentId(UUID.randomUUID().toString());
  }
}
