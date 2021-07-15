/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders;

import com.jasperwireless.api.ws.service.GetSessionInfoResponse;
import com.jasperwireless.api.ws.service.SessionInfoType;
import javax.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.protocol.jasper.infra.ws.JasperWirelessTerminalClient;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.exceptions.SessionProviderException;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class SessionProviderKpn extends SessionProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(SessionProviderKpn.class);

  @Autowired private JasperWirelessTerminalClient jasperWirelessTerminalClient;

  /**
   * Initialization function executed after dependency injection has finished. The SessionProvider
   * Singleton is added to the HashMap of SessionProviderMap.
   */
  @PostConstruct
  public void init() {
    this.sessionProviderMap.addProvider(SessionProviderEnum.KPN, this);
  }

  @Override
  public String getIpAddress(final String iccId) throws OsgpException {
    GetSessionInfoResponse response;
    try {
      response = this.jasperWirelessTerminalClient.getSession(iccId);
    } catch (final SoapFaultClientException e) {
      final String errorMessage =
          String.format("iccId %s is probably not supported in this session provider", iccId);
      LOGGER.error(errorMessage, e);
      throw new FunctionalException(
          FunctionalExceptionType.INVALID_ICCID,
          ComponentType.PROTOCOL_DLMS,
          new OsgpException(ComponentType.PROTOCOL_DLMS, e.getMessage()));
    }

    final SessionInfoType sessionInfoType = this.getSessionInfo(response);

    if (sessionInfoType == null) {
      return null;
    }
    return sessionInfoType.getIpAddress();
  }

  private SessionInfoType getSessionInfo(final GetSessionInfoResponse response)
      throws SessionProviderException {
    if ((response == null)
        || (response.getSessionInfo() == null)
        || (response.getSessionInfo().getSession() == null)) {
      final String errorMessage = String.format("Response Object is not ok: %s", response);
      LOGGER.warn(errorMessage);
      throw new SessionProviderException(errorMessage);
    }
    if (response.getSessionInfo().getSession().size() == 1) {
      return response.getSessionInfo().getSession().get(0);
    }
    return null;
  }
}
