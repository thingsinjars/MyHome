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

package com.myhome.services.unit;

import helpers.TestUtils;
import com.myhome.domain.HouseMember;
import com.myhome.domain.HouseMemberDocument;
import com.myhome.repositories.HouseMemberDocumentRepository;
import com.myhome.repositories.HouseMemberRepository;
import com.myhome.services.springdatajpa.HouseMemberDocumentSDJpaService;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Contains unit tests for the HouseMemberDocumentService class, covering various
 * scenarios such as finding, deleting, updating, and creating house member documents.
 */
public class HouseMemberDocumentServiceTest {

  private static final String MEMBER_ID = "test-member-id";
  private static final String MEMBER_NAME = "test-member-name";
  private static final HouseMemberDocument MEMBER_DOCUMENT =
      new HouseMemberDocument("test-file-name", new byte[0]);
  private static final int COMPRESSION_BORDER_SIZE_KB = 99;
  private static final int MAX_FILE_SIZE_KB = 1;
  private static final long COMPRESSED_IMAGE_QUALITY = (long) 0.99;

  @Mock
  private HouseMemberRepository houseMemberRepository;

  @Mock
  private HouseMemberDocumentRepository houseMemberDocumentRepository;

  @InjectMocks
  private HouseMemberDocumentSDJpaService houseMemberDocumentService;

  /**
   * Initializes Mockito, sets static fields of the `houseMemberDocumentService` object.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(houseMemberDocumentService, "compressionBorderSizeKBytes",
        COMPRESSION_BORDER_SIZE_KB);
    ReflectionTestUtils.setField(houseMemberDocumentService, "maxFileSizeKBytes", MAX_FILE_SIZE_KB);
    ReflectionTestUtils.setField(houseMemberDocumentService, "compressedImageQuality",
        COMPRESSED_IMAGE_QUALITY);
  }

  /**
   * Tests the retrieval of a house member document from a repository. It verifies that
   * the service returns the correct document when the member ID is provided and the
   * repository contains the expected data.
   */
  @Test
  void findMemberDocumentSuccess() {
    // given
    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);
    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.of(testMember));
    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.findHouseMemberDocument(MEMBER_ID);

    // then
    assertTrue(houseMemberDocument.isPresent());
    assertEquals(MEMBER_DOCUMENT, houseMemberDocument.get());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
  }

  /**
   * Tests the functionality of retrieving a house member document when no document is
   * associated with the member. The function verifies that an empty result is returned
   * and that the repository is called with the provided member ID.
   */
  @Test
  void findMemberDocumentNoDocumentPresent() {
    // given
    HouseMember testMember = new HouseMember(MEMBER_ID, null, MEMBER_NAME, null);
    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.of(testMember));
    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.findHouseMemberDocument(MEMBER_ID);

    // then
    assertFalse(houseMemberDocument.isPresent());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
  }

  /**
   * Tests the functionality of finding a house member document when the member does
   * not exist in the database. It verifies that the repository is called with the
   * member ID and that an empty Optional is returned, indicating the member document
   * is not found.
   */
  @Test
  void findMemberDocumentMemberNotExists() {
    // given
    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.empty());
    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.findHouseMemberDocument(MEMBER_ID);

    // then
    assertFalse(houseMemberDocument.isPresent());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
  }

  /**
   * Tests the deletion of a member document by a house member service. It verifies
   * that the document is successfully deleted and that the member's document is set
   * to null after deletion.
   */
  @Test
  void deleteMemberDocumentSuccess() {
    // given
    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);
    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.of(testMember));
    // when
    boolean isDocumentDeleted = houseMemberDocumentService.deleteHouseMemberDocument(MEMBER_ID);

    // then
    assertTrue(isDocumentDeleted);
    assertNull(testMember.getHouseMemberDocument());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberRepository).save(testMember);
  }

  /**
   * Tests the deletion of a house member document when no document is present. It
   * verifies that a document is not deleted and the repository is not saved with any
   * changes.
   */
  @Test
  void deleteMemberDocumentNoDocumentPresent() {
    // given
    HouseMember testMember = new HouseMember(MEMBER_ID, null, MEMBER_NAME, null);
    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.of(testMember));
    // when
    boolean isDocumentDeleted = houseMemberDocumentService.deleteHouseMemberDocument(MEMBER_ID);

    // then
    assertFalse(isDocumentDeleted);
    assertNull(testMember.getHouseMemberDocument());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberRepository, never()).save(testMember);
  }

  /**
   * Tests the deletion of a house member document when the member does not exist. It
   * verifies that the repository is called to check for the member's existence, and
   * no save operation is performed if the member is not found.
   */
  @Test
  void deleteMemberDocumentMemberNotExists() {
    // given
    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.empty());
    // when
    boolean isDocumentDeleted = houseMemberDocumentService.deleteHouseMemberDocument(MEMBER_ID);

    // then
    assertFalse(isDocumentDeleted);
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberRepository, never()).save(any());
  }

  /**
   * Tests the functionality of updating a house member's document. It simulates a
   * successful update by verifying that the new document is saved and associated with
   * the correct house member.
   */
  @Test
  void updateHouseMemberDocumentSuccess() throws IOException {
    // given
    byte[] imageBytes = TestUtils.General.getImageAsByteArray(10, 10);
    MockMultipartFile newDocumentFile = new MockMultipartFile("new-test-file-name", imageBytes);
    HouseMemberDocument savedDocument =
        new HouseMemberDocument(String.format("member_%s_document.jpg", MEMBER_ID), imageBytes);
    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);

    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.of(testMember));
    given(houseMemberDocumentRepository.save(savedDocument))
        .willReturn(savedDocument);
    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.updateHouseMemberDocument(newDocumentFile, MEMBER_ID);

    // then
    assertTrue(houseMemberDocument.isPresent());
    assertEquals(testMember.getHouseMemberDocument(), houseMemberDocument.get());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberDocumentRepository).save(savedDocument);
    verify(houseMemberRepository).save(testMember);
  }

  /**
   * Tests the behavior of the `houseMemberDocumentService` when updating a house member
   * document for a member that does not exist in the database. It verifies that no
   * document is created and the repository is queried as expected.
   */
  @Test
  void updateHouseMemberDocumentMemberNotExists() throws IOException {
    // given
    byte[] imageBytes = TestUtils.General.getImageAsByteArray(10, 10);
    MockMultipartFile newDocumentFile = new MockMultipartFile("new-test-file-name", imageBytes);

    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.empty());

    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.updateHouseMemberDocument(newDocumentFile, MEMBER_ID);

    // then
    assertFalse(houseMemberDocument.isPresent());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberDocumentRepository, never()).save(any());
    verify(houseMemberRepository, never()).save(any());
  }

  /**
   * Tests the update functionality of a house member document service when a file
   * exceeds the allowed size. It expects the service to return an empty optional and
   * not save the document.
   */
  @Test
  void updateHouseMemberDocumentTooLargeFile() throws IOException {
    // given
    byte[] imageBytes = TestUtils.General.getImageAsByteArray(1000, 1000);
    MockMultipartFile tooLargeDocumentFile =
        new MockMultipartFile("new-test-file-name", imageBytes);
    HouseMemberDocument savedDocument =
        new HouseMemberDocument(String.format("member_%s_document.jpg", MEMBER_ID), imageBytes);
    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);

    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.of(testMember));
    given(houseMemberDocumentRepository.save(savedDocument))
        .willReturn(savedDocument);
    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.updateHouseMemberDocument(tooLargeDocumentFile, MEMBER_ID);

    // then
    assertFalse(houseMemberDocument.isPresent());
    assertEquals(testMember.getHouseMemberDocument(), MEMBER_DOCUMENT);
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberDocumentRepository, never()).save(any());
    verify(houseMemberRepository, never()).save(any());
  }

  /**
   * Tests the functionality of creating a new document for a house member. It validates
   * that a new document is successfully saved and associated with the member, and that
   * the existing document is updated with a new filename.
   */
  @Test
  void createHouseMemberDocumentSuccess() throws IOException {
    // given
    byte[] imageBytes = TestUtils.General.getImageAsByteArray(10, 10);
    HouseMemberDocument savedDocument =
        new HouseMemberDocument(String.format("member_%s_document.jpg", MEMBER_ID), imageBytes);
    MockMultipartFile newDocumentFile = new MockMultipartFile("new-test-file-name", imageBytes);
    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);

    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.of(testMember));
    given(houseMemberDocumentRepository.save(savedDocument))
        .willReturn(savedDocument);
    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.createHouseMemberDocument(newDocumentFile, MEMBER_ID);

    // then
    assertTrue(houseMemberDocument.isPresent());
    assertNotEquals(testMember.getHouseMemberDocument().getDocumentFilename(),
        MEMBER_DOCUMENT.getDocumentFilename());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberDocumentRepository).save(savedDocument);
    verify(houseMemberRepository).save(testMember);
  }

  /**
   * Tests the creation of a house member document when a member does not exist in the
   * database. It verifies that the document is not created and that the repository is
   * queried as expected.
   */
  @Test
  void createHouseMemberDocumentMemberNotExists() throws IOException {
    // given
    byte[] imageBytes = TestUtils.General.getImageAsByteArray(10, 10);
    MockMultipartFile newDocumentFile = new MockMultipartFile("new-test-file-name", imageBytes);

    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.empty());
    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.createHouseMemberDocument(newDocumentFile, MEMBER_ID);

    // then
    assertFalse(houseMemberDocument.isPresent());
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberDocumentRepository, never()).save(any());
    verify(houseMemberRepository, never()).save(any());
  }

  /**
   * Tests the creation of a house member document with a file of excessive size. It
   * verifies that the document is not created and no repository save operations occur.
   */
  @Test
  void createHouseMemberDocumentTooLargeFile() throws IOException {
    // given
    byte[] imageBytes = TestUtils.General.getImageAsByteArray(1000, 1000);
    MockMultipartFile tooLargeDocumentFile =
        new MockMultipartFile("new-test-file-name", imageBytes);
    HouseMember testMember = new HouseMember(MEMBER_ID, MEMBER_DOCUMENT, MEMBER_NAME, null);

    given(houseMemberRepository.findByMemberId(MEMBER_ID))
        .willReturn(Optional.of(testMember));
    // when
    Optional<HouseMemberDocument> houseMemberDocument =
        houseMemberDocumentService.createHouseMemberDocument(tooLargeDocumentFile, MEMBER_ID);

    // then
    assertFalse(houseMemberDocument.isPresent());
    assertEquals(testMember.getHouseMemberDocument(), MEMBER_DOCUMENT);
    verify(houseMemberRepository).findByMemberId(MEMBER_ID);
    verify(houseMemberDocumentRepository, never()).save(any());
    verify(houseMemberRepository, never()).save(any());
  }
}
