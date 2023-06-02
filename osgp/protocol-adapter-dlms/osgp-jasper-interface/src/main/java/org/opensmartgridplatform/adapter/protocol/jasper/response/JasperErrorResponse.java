//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.jasper.response;

import lombok.Getter;

@Getter
public class JasperErrorResponse {

  private String errorMessage;
  private String errorCode;

  public JasperErrorResponse() {}

  public JasperErrorResponse(final String errorMessage, final String errorCode) {
    this.errorMessage = errorMessage;
    this.errorCode = errorCode;
  }
}
