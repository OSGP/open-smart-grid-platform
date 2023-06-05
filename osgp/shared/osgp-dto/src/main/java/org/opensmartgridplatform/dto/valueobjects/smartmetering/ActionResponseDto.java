// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
