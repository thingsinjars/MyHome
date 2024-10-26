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

import com.myhome.domain.HouseMemberDocument;
import com.myhome.services.HouseMemberDocumentService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Provides unit tests for the HouseMemberDocumentController class using Mockito for
 * mocking dependencies and JUnit for test execution.
 */
class HouseMemberDocumentTest {

  private static final String MEMBER_ID = "test-member-id";

  private static final MockMultipartFile MULTIPART_FILE =
      new MockMultipartFile("memberDocument", new byte[0]);
  private static final HouseMemberDocument MEMBER_DOCUMENT =
      new HouseMemberDocument(MULTIPART_FILE.getName(), new byte[0]);

  @Mock
  private HouseMemberDocumentService houseMemberDocumentService;

  @InjectMocks
  private HouseMemberDocumentController houseMemberDocumentController;

  /**
   * Initializes Mockito annotations within the test class, enabling mock object creation
   * and setup for subsequent test methods.
   */
  @BeforeEach
  private void init() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Tests the retrieval of a house member document by ID, verifying it returns a
   * successful HTTP response with the document content and image/jpeg content type.
   */
  @Test
  void shouldGetDocumentSuccess() {
    // given
    given(houseMemberDocumentService.findHouseMemberDocument(MEMBER_ID))
        .willReturn(Optional.of(MEMBER_DOCUMENT));
    // when
    ResponseEntity<byte[]> responseEntity =
        houseMemberDocumentController.getHouseMemberDocument(MEMBER_ID);
    //then
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(MEMBER_DOCUMENT.getDocumentContent(), responseEntity.getBody());
    assertEquals(MediaType.IMAGE_JPEG, responseEntity.getHeaders().getContentType());
    verify(houseMemberDocumentService).findHouseMemberDocument(MEMBER_ID);
  }

  /**
   * Tests a controller method that retrieves a house member document by ID. It verifies
   * that when the document service returns an empty Optional, the controller returns
   * a 404 status code.
   */
  @Test
  void shouldGetDocumentFailure() {
    // given
    given(houseMemberDocumentService.findHouseMemberDocument(MEMBER_ID))
        .willReturn(Optional.empty());
    // when
    ResponseEntity<byte[]> responseEntity =
        houseMemberDocumentController.getHouseMemberDocument(MEMBER_ID);
    //then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(houseMemberDocumentService).findHouseMemberDocument(MEMBER_ID);
  }

  /**
   * Tests the uploading of a house member document. It simulates a successful creation
   * of a document, verifies the response status as NO_CONTENT, and checks if the service
   * method was called.
   */
  @Test
  void shouldPostDocumentSuccess() {
    // given
    given(houseMemberDocumentService.createHouseMemberDocument(MULTIPART_FILE, MEMBER_ID))
        .willReturn(Optional.of(MEMBER_DOCUMENT));
    // when
    ResponseEntity<byte[]> responseEntity =
        houseMemberDocumentController.uploadHouseMemberDocument(MEMBER_ID, MULTIPART_FILE);
    //then
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(houseMemberDocumentService).createHouseMemberDocument(MULTIPART_FILE, MEMBER_ID);
  }

  /**
   * Tests a controller's response to a missing document. It simulates a service returning
   * an empty optional, then verifies the controller returns a 404 status code and calls
   * the service as expected.
   */
  @Test
  void shouldPostDocumentFailureNotFound() {
    // given
    given(houseMemberDocumentService.createHouseMemberDocument(MULTIPART_FILE, MEMBER_ID))
        .willReturn(Optional.empty());
    // when
    ResponseEntity<byte[]> responseEntity =
        houseMemberDocumentController.uploadHouseMemberDocument(MEMBER_ID, MULTIPART_FILE);
    //then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(houseMemberDocumentService).createHouseMemberDocument(MULTIPART_FILE, MEMBER_ID);
  }

  /**
   * Tests the functionality of updating a house member document. It verifies that a
   * successful update returns a no-content HTTP status and that the service method is
   * called with the correct parameters.
   */
  @Test
  void shouldPutDocumentSuccess() {
    // given
    given(houseMemberDocumentService.updateHouseMemberDocument(MULTIPART_FILE, MEMBER_ID))
        .willReturn(Optional.of(MEMBER_DOCUMENT));
    // when
    ResponseEntity<byte[]> responseEntity =
        houseMemberDocumentController.updateHouseMemberDocument(MEMBER_ID, MULTIPART_FILE);
    //then
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(houseMemberDocumentService).updateHouseMemberDocument(MULTIPART_FILE, MEMBER_ID);
  }

  /**
   * Tests the `updateHouseMemberDocument` controller method's response when the
   * `houseMemberDocumentService` returns an empty Optional, indicating a document not
   * found. The test verifies a 404 status code is returned and the service method is
   * called correctly.
   */
  @Test
  void shouldPutDocumentFailureNotFound() {
    // given
    given(houseMemberDocumentService.updateHouseMemberDocument(MULTIPART_FILE, MEMBER_ID))
        .willReturn(Optional.empty());
    // when
    ResponseEntity<byte[]> responseEntity =
        houseMemberDocumentController.updateHouseMemberDocument(MEMBER_ID, MULTIPART_FILE);
    //then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(houseMemberDocumentService).updateHouseMemberDocument(MULTIPART_FILE, MEMBER_ID);
  }

  /**
   * Tests the deletion of a house member document. It simulates a successful deletion
   * by returning true from the service and verifies that the controller returns a 204
   * No Content status code and calls the service method correctly.
   */
  @Test
  void shouldDeleteDocumentSuccess() {
    // given
    given(houseMemberDocumentService.deleteHouseMemberDocument(MEMBER_ID))
        .willReturn(true);
    // when
    ResponseEntity responseEntity =
        houseMemberDocumentController.deleteHouseMemberDocument(MEMBER_ID);
    //then
    assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    verify(houseMemberDocumentService).deleteHouseMemberDocument(MEMBER_ID);
  }

  /**
   * Tests the deletion of a house member document when it is not found.
   * It calls the `deleteHouseMemberDocument` method in the controller with a specific
   * ID.
   * The test verifies that the service is called with the correct ID and that a NOT_FOUND
   * response is returned.
   */
  @Test
  void shouldDeleteDocumentFailureNotFound() {
    // given
    given(houseMemberDocumentService.deleteHouseMemberDocument(MEMBER_ID))
        .willReturn(false);
    // when
    ResponseEntity responseEntity =
        houseMemberDocumentController.deleteHouseMemberDocument(MEMBER_ID);
    //then
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(houseMemberDocumentService).deleteHouseMemberDocument(MEMBER_ID);
  }
}
