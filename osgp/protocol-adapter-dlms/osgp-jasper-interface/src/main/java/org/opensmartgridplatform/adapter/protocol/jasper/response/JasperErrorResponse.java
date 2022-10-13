/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
