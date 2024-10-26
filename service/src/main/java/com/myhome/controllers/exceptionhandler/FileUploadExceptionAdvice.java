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

package com.myhome.controllers.exceptionhandler;

import java.io.IOException;
import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Handles exceptions related to file uploads, providing standardized error responses.
 * It catches MaxUploadSizeExceededException and IOException, returning a ResponseEntity
 * with a specific HTTP status code and error message.
 */
@ControllerAdvice
public class FileUploadExceptionAdvice {

  /**
   * Handles MaxUploadSizeExceededException exceptions by returning a HTTP 414 status
   * code with a JSON response containing an error message indicating that the file
   * size exceeds the limit.
   *
   * @param exc exception thrown when the file size exceeds the maximum allowed upload
   * size.
   *
   * @returns a ResponseEntity with a 414 status code and a JSON body containing a message.
   */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity handleMaxSizeException(MaxUploadSizeExceededException exc) {
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(new HashMap<String, String>() {{
      put("message", "File size exceeds limit!");
    }});
  }

  /**
   * Handles IOException exceptions by returning a ResponseEntity with a status of 409
   * Conflict and a HashMap containing an error message.
   *
   * @param exc MaxUploadSizeExceededException exception that triggered the exception
   * handling.
   *
   * @returns a ResponseEntity with a status of HttpStatus.CONFLICT and a JSON payload.
   */
  @ExceptionHandler(IOException.class)
  public ResponseEntity handleIOException(MaxUploadSizeExceededException exc) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new HashMap<String, String>() {{
      put("message", "Something go wrong with document saving!");
    }});
  }
}

