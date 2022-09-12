/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.jasper.rest.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.opensmartgridplatform.adapter.protocol.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.adapter.protocol.jasper.rest.JasperError;
import org.opensmartgridplatform.adapter.protocol.jasper.rest.config.JasperWirelessRestAccess;
import org.opensmartgridplatform.adapter.protocol.jasper.rest.json.JasperErrorResponse;
import org.springframework.web.client.HttpStatusCodeException;

public class JasperWirelessRestClient {

  protected void handleException(final HttpStatusCodeException e) throws OsgpJasperException {
    final JasperError jasperError = this.getJasperError(e);
    if (jasperError != null) {
      throw new OsgpJasperException(jasperError);
    } else {
      throw new OsgpJasperException(e.getResponseBodyAsString(), e);
    }
  }

  protected String createAuthorizationCredentials(
      final JasperWirelessRestAccess jasperWirelessRestAccess) {
    return Base64.getEncoder()
        .encodeToString(
            (jasperWirelessRestAccess.getUsername()
                    + ":"
                    + jasperWirelessRestAccess.getLicenseKey())
                .getBytes());
  }

  private JasperError getJasperError(final HttpStatusCodeException httpStatusCodeException) {
    final ObjectMapper objectMapper = new ObjectMapper();
    JasperErrorResponse error = null;
    try {
      error =
          objectMapper.readValue(
              httpStatusCodeException.getResponseBodyAsString(), JasperErrorResponse.class);
      return JasperError.getByCode(error.getErrorCode());
    } catch (final JsonProcessingException | IllegalArgumentException e) {
      return null;
    }
  }
}
