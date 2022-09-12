/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.jasper.rest.client;

import org.opensmartgridplatform.adapter.protocol.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.adapter.protocol.jasper.rest.config.JasperWirelessRestAccess;
import org.opensmartgridplatform.adapter.protocol.jasper.rest.json.GetSessionInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class JasperWirelessTerminalRestClient extends JasperWirelessRestClient {

  private static final String SERVICE_GET_SESSION_INFO = "%s/rws/api/%s/devices/%s/sessionInfo";

  @Autowired private RestTemplate jasperwirelessRestTemplate;

  @Autowired private JasperWirelessRestAccess jasperWirelessRestAccess;

  public GetSessionInfoResponse getSession(final String iccId) throws OsgpJasperException {

    final String authorizationCredentials =
        this.createAuthorizationCredentials(this.jasperWirelessRestAccess);

    final String url =
        String.format(
            SERVICE_GET_SESSION_INFO,
            this.jasperWirelessRestAccess.getUrl(),
            this.jasperWirelessRestAccess.getApiVersion(),
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

      getSessionInfoResponse = getSessionInfoResponseEntity.getBody();

    } catch (final HttpClientErrorException | HttpServerErrorException e) {
      this.handleException(e);
    } catch (final RestClientException e) {
      throw new OsgpJasperException(e.getMessage(), e);
    }
    return getSessionInfoResponse;
  }
}
