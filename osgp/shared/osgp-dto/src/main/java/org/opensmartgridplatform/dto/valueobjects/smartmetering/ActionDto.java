// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class ActionDto implements Serializable {
  private static final long serialVersionUID = 7098808264405613224L;

  private ActionRequestDto request;
  private ActionResponseDto response;

  public ActionDto(final ActionRequestDto request) {
    this.request = request;
  }

  public ActionRequestDto getRequest() {
    return this.request;
  }

  public ActionResponseDto getResponse() {
    return this.response;
  }

  public void setResponse(final ActionResponseDto response) {
    this.response = response;
  }
}
