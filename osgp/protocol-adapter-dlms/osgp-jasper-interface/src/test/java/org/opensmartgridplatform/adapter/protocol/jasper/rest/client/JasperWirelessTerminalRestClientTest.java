//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.jasper.rest.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.jasper.config.JasperWirelessAccess;
import org.opensmartgridplatform.adapter.protocol.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.adapter.protocol.jasper.response.GetSessionInfoResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class JasperWirelessTerminalRestClientTest {

  private static final String SERVICE_GET_SESSION_INFO = "/rws/api/%s/devices/%s/sessionInfo";
  private static final String ICCID = "12345";
  private static final String USERNAME = "user";
  private static final String APIKEY = "1234-abcd-5678-ef";
  private static final String BASEURL = "http://localhost:8081";
  private static final String APIVERSION = "v1";
  private static final String URL =
      String.format(BASEURL + SERVICE_GET_SESSION_INFO, APIVERSION, ICCID);

  private static final String IP_ADDRESS = "1.2.3.4";
  private static final String IPV6_ADDRESS = "1.2.3.4.5.6";

  private enum SessionType {
    NEVER_HAD_SESSION,
    CURRENT_SESSION,
    EXPIRED_SESSION
  };

  @Mock private RestTemplate jasperwirelessRestTemplate;

  @Mock private JasperWirelessAccess jasperWirelessAccess;

  @InjectMocks private JasperWirelessTerminalRestClient jasperWirelessTerminalRestClient;

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
            eq(URL), eq(HttpMethod.GET), any(HttpEntity.class), eq(GetSessionInfoResponse.class)))
        .thenReturn(this.createResponseEntity(HttpStatus.OK, SessionType.CURRENT_SESSION));

    final GetSessionInfoResponse getSessionInfoResponse =
        this.jasperWirelessTerminalRestClient.getSession(ICCID);

    assertEquals(ICCID, getSessionInfoResponse.getIccid());
    assertEquals(IP_ADDRESS, getSessionInfoResponse.getIpAddress());
  }

  @Test
  public void testExpiredSession() throws OsgpJasperException {

    when(this.jasperwirelessRestTemplate.exchange(
            eq(URL), eq(HttpMethod.GET), any(HttpEntity.class), eq(GetSessionInfoResponse.class)))
        .thenReturn(this.createResponseEntity(HttpStatus.OK, SessionType.EXPIRED_SESSION));

    final GetSessionInfoResponse getSessionInfoResponse =
        this.jasperWirelessTerminalRestClient.getSession(ICCID);

    assertEquals(ICCID, getSessionInfoResponse.getIccid());
    assertNull(getSessionInfoResponse.getIpAddress());
  }

  @Test
  public void testNeverHadSession() throws OsgpJasperException {

    when(this.jasperwirelessRestTemplate.exchange(
            eq(URL), eq(HttpMethod.GET), any(HttpEntity.class), eq(GetSessionInfoResponse.class)))
        .thenReturn(this.createResponseEntity(HttpStatus.OK, SessionType.NEVER_HAD_SESSION));

    final GetSessionInfoResponse getSessionInfoResponse =
        this.jasperWirelessTerminalRestClient.getSession(ICCID);

    assertEquals(ICCID, getSessionInfoResponse.getIccid());
    assertNull(getSessionInfoResponse.getIpAddress());
  }

  @Test
  public void testInvalidIccId() throws OsgpJasperException {

    when(this.jasperwirelessRestTemplate.exchange(
            eq(URL), eq(HttpMethod.GET), any(HttpEntity.class), eq(GetSessionInfoResponse.class)))
        .thenThrow(this.createHttpClientErrorException());

    final OsgpJasperException osgpJasperException =
        assertThrows(
            OsgpJasperException.class,
            () -> this.jasperWirelessTerminalRestClient.getSession(ICCID));
    assertEquals("20000001:Resource not found - Invalid ICCID.", osgpJasperException.getMessage());
    assertNotNull(osgpJasperException.getJasperError());
    assertEquals(HttpStatus.NOT_FOUND, osgpJasperException.getJasperError().getHttpStatus());
  }

  @Test
  public void testUnmappedErrorException() throws OsgpJasperException {

    when(this.jasperwirelessRestTemplate.exchange(
            eq(URL), eq(HttpMethod.GET), any(HttpEntity.class), eq(GetSessionInfoResponse.class)))
        .thenThrow(this.createUnmappedErrorException());

    final OsgpJasperException osgpJasperException =
        assertThrows(
            OsgpJasperException.class,
            () -> this.jasperWirelessTerminalRestClient.getSession(ICCID));
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

  private ResponseEntity<GetSessionInfoResponse> createResponseEntity(
      final HttpStatus httpStatus, final SessionType sessionType) {
    Date endSession;
    Date startSession;
    String ipAddress;
    switch (sessionType) {
      case EXPIRED_SESSION:
        startSession = new Date(Instant.now().minus(6, ChronoUnit.MINUTES).toEpochMilli());
        endSession = new Date(Instant.now().minus(1, ChronoUnit.MINUTES).toEpochMilli());
        ipAddress = IP_ADDRESS;
        break;
      case CURRENT_SESSION:
        startSession = new Date(Instant.now().minus(1, ChronoUnit.MINUTES).toEpochMilli());
        endSession = null;
        ipAddress = IP_ADDRESS;
        break;
      case NEVER_HAD_SESSION:
        startSession = null;
        endSession = null;
        ipAddress = null;
      default:
        startSession = null;
        endSession = null;
        ipAddress = null;
    }
    final GetSessionInfoResponse getSessionInfoResponse =
        new GetSessionInfoResponse(ICCID, ipAddress, IPV6_ADDRESS, startSession, endSession);
    return new ResponseEntity<GetSessionInfoResponse>(getSessionInfoResponse, httpStatus);
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
