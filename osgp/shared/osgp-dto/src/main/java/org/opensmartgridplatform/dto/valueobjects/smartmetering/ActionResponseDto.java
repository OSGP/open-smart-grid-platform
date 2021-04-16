/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class ActionResponseDto implements Serializable {

  private static final long serialVersionUID = -6579443565899923397L;
  private OsgpResultTypeDto result;
  private String exception;
  private String resultString;

  public ActionResponseDto() {
    // default constructor
  }

  public ActionResponseDto(final String resultString) {
    this(OsgpResultTypeDto.OK, null, resultString);
  }

  public ActionResponseDto(final Exception exception, final String resultString) {
    this(
        exception == null ? OsgpResultTypeDto.OK : OsgpResultTypeDto.NOT_OK,
        exception,
        resultString);
  }

  public ActionResponseDto(
      final OsgpResultTypeDto result, final Exception exception, final String resultString) {
    this.result = result;
    if (exception != null) {
      this.exception = exception.toString();
    }
    this.resultString = resultString;
  }

  public OsgpResultTypeDto getResult() {
    return this.result;
  }

  public void setResult(final OsgpResultTypeDto result) {
    this.result = result;
  }

  public String getException() {
    return this.exception;
  }

  public void setException(final Exception exception) {
    this.exception = exception.toString();
  }

  public boolean hasException() {
    return this.exception != null;
  }

  public String getResultString() {
    return this.resultString;
  }

  public void setResultString(final String resultString) {
    this.resultString = resultString;
  }
}
