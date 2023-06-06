// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.rest.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.nio.charset.Charset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.jasper.config.JasperWirelessAccess;
import org.opensmartgridplatform.adapter.protocol.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.adapter.protocol.jasper.rest.json.SendSMSResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class JasperWirelessSmsRestClientTest {

  private static final String SERVICE_SMS_SEND_SMS = "/rws/api/%s/devices/%s/smsMessages";
  private static final String ICCID = "12345";
  private static final String USERNAME = "user";
  private static final String APIKEY = "1234-abcd-5678-ef";
  private static final int SMSMSGID = 67890;
  private static final String BASEURL = "http://localhost";
  private static final String APIVERSION = "v1";
  private static final String APITYPE = "REST";
  private static final String URL =
      String.format(BASEURL + SERVICE_SMS_SEND_SMS, APIVERSION, ICCID);

  @Mock private RestTemplate jasperwirelessRestTemplate;

  @Mock private JasperWirelessAccess jasperWirelessAccess;

  @InjectMocks private JasperWirelessSmsRestClient jasperWirelessSmsRestClient;

  @BeforeEach
  private void init() {
    when(this.jasperWirelessAccess.getUri()).thenReturn(BASEURL);
    when(this.jasperWirelessAccess.getApiVersion()).thenReturn(APIVERSION);
    when(this.jasperWirelessAccess.getUsername()).thenReturn(USERNAME);
    when(this.jasperWirelessAccess.getApiKey()).thenReturn(APIKEY);
  }

  @Test
  public void testOk() throws OsgpJasperException {

    when(this.jasperwirelessRestTemplate.exchange(
            eq(URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(SendSMSResponse.class)))
        .thenReturn(this.createResponseEntity(HttpStatus.OK));

    final org.opensmartgridplatform.adapter.protocol.jasper.response.SendSMSResponse
        sendSMSResponse = this.jasperWirelessSmsRestClient.sendWakeUpSMS(ICCID);

    assertEquals(SMSMSGID, sendSMSResponse.getSmsMsgId());
  }

  @Test
  public void testInvalidIccId() throws OsgpJasperException {

    when(this.jasperwirelessRestTemplate.exchange(
            eq(URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(SendSMSResponse.class)))
        .thenThrow(this.createHttpClientErrorException());

    final OsgpJasperException osgpJasperException =
        assertThrows(
            OsgpJasperException.class, () -> this.jasperWirelessSmsRestClient.sendWakeUpSMS(ICCID));
    assertEquals("20000001:Resource not found - Invalid ICCID.", osgpJasperException.getMessage());
    assertNotNull(osgpJasperException.getJasperError());
    assertEquals(HttpStatus.NOT_FOUND, osgpJasperException.getJasperError().getHttpStatus());
  }

  @Test
  public void testUnmappedErrorException() throws OsgpJasperException {

    when(this.jasperwirelessRestTemplate.exchange(
            eq(URL), eq(HttpMethod.POST), any(HttpEntity.class), eq(SendSMSResponse.class)))
        .thenThrow(this.createUnmappedErrorException());

    final OsgpJasperException osgpJasperException =
        assertThrows(
            OsgpJasperException.class, () -> this.jasperWirelessSmsRestClient.sendWakeUpSMS(ICCID));
    assertEquals(
        "{\"errorMessage\":\"Some unmapped error.\",\"errorCode\":\"99999999\"}",
        osgpJasperException.getMessage());
    assertNull(osgpJasperException.getJasperError());
  }

  private Throwable createHttpClientErrorException() {
    final String body =
        "{\"errorMessage\":\"Resource not found - Invalid ICCID\",\"errorCode\":\"20000001\"}";
    final String statusText = "NOT_FOUND";
    return new HttpClientErrorException(
        HttpStatus.NOT_FOUND,
        statusText,
        body.getBytes(Charset.defaultCharset()),
        Charset.defaultCharset());
  }

  private ResponseEntity<SendSMSResponse> createResponseEntity(final HttpStatus httpStatus) {
    final SendSMSResponse sendSMSResponse = new SendSMSResponse(SMSMSGID);
    return new ResponseEntity<SendSMSResponse>(sendSMSResponse, httpStatus);
  }

  private Throwable createUnmappedErrorException() {
    final String body = "{\"errorMessage\":\"Some unmapped error.\",\"errorCode\":\"99999999\"}";

    final String statusText = "BAD_REQUEST";
    return new HttpServerErrorException(
        HttpStatus.BAD_REQUEST,
        statusText,
        body.getBytes(Charset.defaultCharset()),
        Charset.defaultCharset());
  }
}
