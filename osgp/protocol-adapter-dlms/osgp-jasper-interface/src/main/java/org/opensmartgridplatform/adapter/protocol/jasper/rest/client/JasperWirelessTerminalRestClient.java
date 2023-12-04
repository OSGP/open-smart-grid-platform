// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.rest.client;

import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessTerminalClient;
import org.opensmartgridplatform.adapter.protocol.jasper.response.GetSessionInfoResponse;
import org.opensmartgridplatform.jasper.config.JasperWirelessAccess;
import org.opensmartgridplatform.jasper.exceptions.OsgpJasperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class JasperWirelessTerminalRestClient extends JasperWirelessRestClient
    implements JasperWirelessTerminalClient {

  private static final String SERVICE_GET_SESSION_INFO = "%s/rws/api/%s/devices/%s/sessionInfo";

  @Autowired private RestTemplate jasperwirelessRestTemplate;

  @Autowired private JasperWirelessAccess jasperWirelessAccess;

  @Override
  public GetSessionInfoResponse getSession(final String iccId) throws OsgpJasperException {

    final String authorizationCredentials =
        this.createAuthorizationCredentials(this.jasperWirelessAccess);

    final String url =
        String.format(
            SERVICE_GET_SESSION_INFO,
            this.jasperWirelessAccess.getUri(),
            this.jasperWirelessAccess.getApiVersion(),
            iccId);

    final HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    headers.add(HttpHeaders.AUTHORIZATION, "Basic " + authorizationCredentials);

    final HttpEntity<String> entity = new HttpEntity<>(headers);

    GetSessionInfoResponse getSessionInfoResponse = null;
    try {
      final ResponseEntity<GetSessionInfoResponse> getSessionInfoResponseEntity =
          this.jasperwirelessRestTemplate.exchange(
              url, HttpMethod.GET, entity, GetSessionInfoResponse.class);

      getSessionInfoResponse = this.checkOnSessionValidity(getSessionInfoResponseEntity);

    } catch (final HttpClientErrorException | HttpServerErrorException e) {
      this.handleException(e);
    } catch (final RestClientException e) {
      throw new OsgpJasperException(e.getMessage(), e);
    }
    return getSessionInfoResponse;
  }

  private GetSessionInfoResponse checkOnSessionValidity(
      final ResponseEntity<GetSessionInfoResponse> getSessionInfoResponseEntity) {
    // To simulated to same behaviour as the SOAP interface. Session info of an expired session is
    // removed form the response.
    // REST-interface returns information about the current or most recent data session for a given
    // device.
    // SOAP-interface returns the current session information (IP address and session start time)
    // for one or more devices. If the specified device is not in session, no information is
    // returned.
    final GetSessionInfoResponse getSessionInfoResponse = getSessionInfoResponseEntity.getBody();
    if (this.hasCurrentSession(getSessionInfoResponse)) {
      return getSessionInfoResponse;
    } else {
      return new GetSessionInfoResponse(getSessionInfoResponse.getIccid(), null, null, null, null);
    }
  }

  private boolean hasCurrentSession(final GetSessionInfoResponse getSessionInfoResponse) {
    return (getSessionInfoResponse.getDateSessionStarted() != null
        && getSessionInfoResponse.getDateSessionEnded() == null);
  }
}
