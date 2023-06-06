// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.infra.ws;

import com.jasperwireless.api.ws.service.GetSessionInfoRequest;
import com.jasperwireless.api.ws.service.GetSessionInfoResponse;
import com.jasperwireless.api.ws.service.ObjectFactory;
import com.jasperwireless.api.ws.service.SessionInfoType;
import lombok.extern.slf4j.Slf4j;
import org.apache.ws.security.WSConstants;
import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessTerminalClient;
import org.opensmartgridplatform.adapter.protocol.jasper.config.JasperWirelessAccess;
import org.opensmartgridplatform.adapter.protocol.jasper.exceptions.OsgpJasperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;

@Slf4j
public class JasperWirelessTerminalSoapClient implements JasperWirelessTerminalClient {

  private static final String SERVICE_SESSION_INFO =
      "http://api.jasperwireless.com/ws/service/terminal/GetSessionInfo";

  @Autowired private WebServiceTemplate jasperWebServiceTemplate;

  private static final ObjectFactory WS_CLIENT_FACTORY = new ObjectFactory();

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  @Autowired private JasperWirelessAccess jasperWirelessAccess;

  @Override
  public org.opensmartgridplatform.adapter.protocol.jasper.response.GetSessionInfoResponse
      getSession(final String iccid) throws OsgpJasperException {
    final GetSessionInfoRequest getSessionInfoRequest =
        WS_CLIENT_FACTORY.createGetSessionInfoRequest();

    getSessionInfoRequest.setLicenseKey(this.jasperWirelessAccess.getLicenseKey());
    getSessionInfoRequest.setMessageId(
        this.correlationIdProviderService.getCorrelationId("messageID", iccid));
    getSessionInfoRequest.setVersion(this.jasperWirelessAccess.getApiVersion());
    getSessionInfoRequest.getIccid().add(iccid);

    for (final ClientInterceptor interceptor : this.jasperWebServiceTemplate.getInterceptors()) {
      if (interceptor instanceof Wss4jSecurityInterceptor) {
        setUsernameToken(
            (Wss4jSecurityInterceptor) interceptor,
            this.jasperWirelessAccess.getUsername(),
            this.jasperWirelessAccess.getPassword());
      }
    }

    // override default uri
    this.jasperWebServiceTemplate.setDefaultUri(this.jasperWirelessAccess.getUri());

    return this.convertResponse(
        (GetSessionInfoResponse)
            this.jasperWebServiceTemplate.marshalSendAndReceive(
                getSessionInfoRequest, new SoapActionCallback(SERVICE_SESSION_INFO)));
  }

  private org.opensmartgridplatform.adapter.protocol.jasper.response.GetSessionInfoResponse
      convertResponse(final GetSessionInfoResponse response) throws OsgpJasperException {
    this.validateResponse(response);
    return this.convertSessionInfo(response.getSessionInfo().getSession().get(0));
  }

  private void validateResponse(final GetSessionInfoResponse response) throws OsgpJasperException {
    if ((response == null)
        || (response.getSessionInfo() == null)
        || (response.getSessionInfo().getSession() == null)
        || (response.getSessionInfo().getSession().isEmpty())) {
      final String errorMessage = String.format("Response Object is not ok: %s", response);
      log.warn(errorMessage);
      throw new OsgpJasperException(errorMessage);
    }
  }

  private org.opensmartgridplatform.adapter.protocol.jasper.response.GetSessionInfoResponse
      convertSessionInfo(final SessionInfoType sessionInfoType) {
    final String ipV6Address = null;
    return new org.opensmartgridplatform.adapter.protocol.jasper.response.GetSessionInfoResponse(
        sessionInfoType.getIccid(),
        sessionInfoType.getIpAddress(),
        ipV6Address,
        sessionInfoType.getDateSessionStarted().toGregorianCalendar().getTime(),
        sessionInfoType.getDateSessionEnded().toGregorianCalendar().getTime());
  }

  private static void setUsernameToken(
      final Wss4jSecurityInterceptor interceptor, final String user, final String pass) {
    interceptor.setSecurementUsername(user);
    interceptor.setSecurementPassword(pass);
    interceptor.setSecurementPasswordType(WSConstants.PW_TEXT);
  }
}
