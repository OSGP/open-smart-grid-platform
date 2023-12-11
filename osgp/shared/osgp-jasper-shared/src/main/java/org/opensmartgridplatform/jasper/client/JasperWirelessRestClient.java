// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.jasper.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import org.opensmartgridplatform.jasper.config.JasperWirelessAccess;
import org.opensmartgridplatform.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.jasper.response.JasperErrorResponse;
import org.opensmartgridplatform.jasper.rest.JasperError;
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
      final JasperWirelessAccess jasperWirelessRestAccess) {
    return Base64.getEncoder()
        .encodeToString(
            (jasperWirelessRestAccess.getUsername() + ":" + jasperWirelessRestAccess.getApiKey())
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
