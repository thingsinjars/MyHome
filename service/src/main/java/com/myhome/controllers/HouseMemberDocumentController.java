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

import com.myhome.api.DocumentsApi;
import com.myhome.domain.HouseMemberDocument;
import com.myhome.services.HouseMemberDocumentService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller which provides endpoints for managing house member documents
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class HouseMemberDocumentController implements DocumentsApi {

  private final HouseMemberDocumentService houseMemberDocumentService;

  /**
   * retrieves a house member's document content from the service and returns it as a
   * byte array with appropriate headers for caching and filename.
   * 
   * @param memberId identifier of the house member for whom the document is being retrieved.
   * 
   * @returns a `ResponseEntity` object containing the requested document content and
   * headers.
   */
  @Override
  public ResponseEntity<byte[]> getHouseMemberDocument(@PathVariable String memberId) {
    log.trace("Received request to get house member documents");
    Optional<HouseMemberDocument> houseMemberDocumentOptional =
        houseMemberDocumentService.findHouseMemberDocument(memberId);

    return houseMemberDocumentOptional.map(document -> {

      HttpHeaders headers = new HttpHeaders();
      byte[] content = document.getDocumentContent();

      headers.setCacheControl(CacheControl.noCache().getHeaderValue());
      headers.setContentType(MediaType.IMAGE_JPEG);

      ContentDisposition contentDisposition = ContentDisposition
          .builder("inline")
          .filename(document.getDocumentFilename())
          .build();

      headers.setContentDisposition(contentDisposition);

      return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  /**
   * receives a multipart file and an member id, creates a new house member document
   * using the file data and id, and returns a response entity based on whether the
   * creation was successful or not.
   * 
   * @param memberId identifier of the house member whose document is being uploaded.
   * 
   * @param memberDocument document to be uploaded for the specified member ID, which
   * is passed through the `@PathVariable` annotation.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether a valid house member document was created successfully.
   */
  @Override
  public ResponseEntity uploadHouseMemberDocument(
      @PathVariable String memberId, @RequestParam("memberDocument") MultipartFile memberDocument) {
    log.trace("Received request to add house member documents");

    Optional<HouseMemberDocument> houseMemberDocumentOptional =
        houseMemberDocumentService.createHouseMemberDocument(memberDocument, memberId);
    return houseMemberDocumentOptional
        .map(document -> ResponseEntity.status(HttpStatus.NO_CONTENT).build())
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  /**
   * updates a house member's document, given their ID and a multipart file representing
   * the document.
   * 
   * @param memberId unique identifier of the house member whose document is being updated.
   * 
   * @param memberDocument file containing the updated house member document to be saved
   * into the database by the `houseMemberDocumentService`.
   * 
   * @returns a `ResponseEntity` object with a status code of either `NO_CONTENT` or
   * `NOT_FOUND`, depending on whether the update was successful.
   */
  @Override
  public ResponseEntity updateHouseMemberDocument(
      @PathVariable String memberId, @RequestParam("memberDocument") MultipartFile memberDocument) {
    log.trace("Received request to update house member documents");
    Optional<HouseMemberDocument> houseMemberDocumentOptional =
        houseMemberDocumentService.updateHouseMemberDocument(memberDocument, memberId);
    return houseMemberDocumentOptional
        .map(document -> ResponseEntity.status(HttpStatus.NO_CONTENT).build())
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  /**
   * deletes a house member document based on the provided `memberId`. If successful,
   * it returns a `ResponseEntity` with a status code of `NO_CONTENT`.
   * 
   * @param memberId ID of the house member whose document is to be deleted.
   * 
   * @returns a `ResponseEntity` with a `NO_CONTENT` status code if the document was
   * successfully deleted, or a `NOT_FOUND` status code otherwise.
   */
  @Override
  public ResponseEntity<Void> deleteHouseMemberDocument(@PathVariable String memberId) {
    log.trace("Received request to delete house member documents");
    boolean isDocumentDeleted = houseMemberDocumentService.deleteHouseMemberDocument(memberId);
    if (isDocumentDeleted) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }
}
























