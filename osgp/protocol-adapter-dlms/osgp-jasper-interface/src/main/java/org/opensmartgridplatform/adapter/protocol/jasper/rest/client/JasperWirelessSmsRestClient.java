/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.jasper.rest.client;

import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessSmsClient;
import org.opensmartgridplatform.adapter.protocol.jasper.config.JasperWirelessAccess;
import org.opensmartgridplatform.adapter.protocol.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.adapter.protocol.jasper.rest.json.SendSMSRequest;
import org.opensmartgridplatform.adapter.protocol.jasper.rest.json.SendSMSResponse;
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

public class JasperWirelessSmsRestClient extends JasperWirelessRestClient
    implements JasperWirelessSmsClient {

  private static final String SERVICE_SMS_SEND_SMS = "%s/rws/api/%s/devices/%s/smsMessages";

  @Autowired private RestTemplate jasperwirelessRestTemplate;

  @Autowired private JasperWirelessAccess jasperWirelessAccess;

  @Autowired private short jasperGetValidityPeriod;

  @Override
  public org.opensmartgridplatform.adapter.protocol.jasper.response.SendSMSResponse sendWakeUpSMS(
      final String iccId) throws OsgpJasperException {

    final SendSMSRequest sendSMSRequest = new SendSMSRequest();
    sendSMSRequest.setMessageText("");
    sendSMSRequest.setTpvp(this.jasperGetValidityPeriod);

    final String authorizationCredentials =
        this.createAuthorizationCredentials(this.jasperWirelessAccess);

    final String url =
        String.format(
            SERVICE_SMS_SEND_SMS,
            this.jasperWirelessAccess.getUri(),
            this.jasperWirelessAccess.getApiVersion(),
            iccId);

    final HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    headers.add(HttpHeaders.AUTHORIZATION, "Basic " + authorizationCredentials);
    final HttpEntity<SendSMSRequest> entity = new HttpEntity<>(sendSMSRequest, headers);

    long smsMessageId = 0;
    try {
      final ResponseEntity<SendSMSResponse> sendSMSResponseEntity =
          this.jasperwirelessRestTemplate.exchange(
              url, HttpMethod.POST, entity, SendSMSResponse.class);

      final SendSMSResponse sendSmsResponse = sendSMSResponseEntity.getBody();
      smsMessageId = sendSmsResponse.getSmsMessageId();
    } catch (final HttpClientErrorException | HttpServerErrorException e) {
      this.handleException(e);
    } catch (final RestClientException e) {
      throw new OsgpJasperException(e.getMessage(), e);
    }

    return new org.opensmartgridplatform.adapter.protocol.jasper.response.SendSMSResponse(
        smsMessageId);
  }
}
