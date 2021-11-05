/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.throttling.web.api;

import org.opensmartgridplatform.throttling.api.ApiException;
import org.opensmartgridplatform.throttling.api.NonUniqueRequestIdException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ThrottlingResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({NonUniqueRequestIdException.class})
  protected ResponseEntity<Object> handleApiException(
      final Exception exception, final WebRequest request) {

    return this.handleExceptionInternal(
        exception,
        ((ApiException) exception).asJsonNode(),
        new HttpHeaders(),
        HttpStatus.valueOf(((ApiException) exception).statusCode()),
        request);
  }
}
