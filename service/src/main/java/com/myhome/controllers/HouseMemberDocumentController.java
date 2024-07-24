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
 * Is a REST Controller that manages house member documents through various HTTP
 * requests. It provides endpoints for retrieving, uploading, updating, and deleting
 * documents associated with a given member ID. The controller delegates tasks to the
 * HouseMemberDocumentService class.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class HouseMemberDocumentController implements DocumentsApi {

  private final HouseMemberDocumentService houseMemberDocumentService;

  /**
   * Retrieves a house member document based on the provided `memberId`, and returns
   * it as a response with headers set for caching, content type, and filename. If no
   * document is found, a NOT_FOUND status is returned instead.
   * 
   * @param memberId identification number of a house member used to retrieve their
   * document from the service.
   * 
   * @returns a ResponseEntity containing a byte array of an image.
   * 
   * Returns a ResponseEntity containing a byte array content and HTTP headers. The
   * HTTP headers include cache control to prevent caching, content type as IMAGE_JPEG,
   * and content disposition with filename. The status is either OK or NOT_FOUND.
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
   * Uploads a member document for a specified house member and returns a response
   * indicating success or failure. It calls the `houseMemberDocumentService` to create
   * a new document, mapping the result to either a successful (NO_CONTENT) or failed
   * (NOT_FOUND) response.
   * 
   * @param memberId ID of a house member and is used to create a new HouseMemberDocument
   * entity along with the uploaded file.
   * 
   * @param memberDocument MultipartFile containing the uploaded document for the
   * specified house member.
   * 
   * • `MultipartFile`: It represents a file that has been uploaded via HTTP request.
   * 
   * @returns a `ResponseEntity` with HTTP status code.
   * 
   * Returns an instance of ResponseEntity with either HTTP status NO_CONTENT or
   * NOT_FOUND. If a HouseMemberDocument is successfully created, the response contains
   * a NO_CONTENT status; otherwise, it contains a NOT_FOUND status.
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
   * Updates a house member document based on the provided `memberId` and `memberDocument`.
   * If the update is successful, it returns a HTTP response with status code 204 (NO
   * CONTENT). Otherwise, it returns a response with status code 404 (NOT FOUND) if the
   * document cannot be found.
   * 
   * @param memberId identifier of the house member whose document is to be updated.
   * 
   * @param memberDocument multipart file that needs to be updated for the specified
   * house member with the provided `memberId`.
   * 
   * @returns a ResponseEntity with either HTTP status NO_CONTENT or NOT_FOUND.
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
   * Deletes house member documents based on the provided member ID. If the deletion
   * is successful, it returns a 204 response (No Content) indicating the operation was
   * successful. If the document does not exist, it returns a 404 response (Not Found).
   * 
   * @param memberId identification number of a house member and is used to identify
   * the document to be deleted from the service.
   * 
   * @returns a ResponseEntity with HTTP status codes.
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
