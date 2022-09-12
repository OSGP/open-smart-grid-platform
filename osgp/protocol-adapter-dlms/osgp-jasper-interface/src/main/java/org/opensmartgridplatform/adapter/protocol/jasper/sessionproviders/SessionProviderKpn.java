/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders;

import javax.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.protocol.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.adapter.protocol.jasper.rest.client.JasperWirelessTerminalRestClient;
import org.opensmartgridplatform.adapter.protocol.jasper.rest.json.GetSessionInfoResponse;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class SessionProviderKpn extends SessionProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(SessionProviderKpn.class);

  @Autowired private JasperWirelessTerminalRestClient jasperWirelessTerminalRestClient;

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
    GetSessionInfoResponse response = null;
    try {
      response = this.jasperWirelessTerminalRestClient.getSession(iccId);
    } catch (final OsgpJasperException e) {
      this.handleException(iccId, e);
    }
    return response.getIpAddress();
  }

  private void handleException(final String iccId, final OsgpJasperException e)
      throws FunctionalException {
    String errorMessage = "";
    FunctionalExceptionType functionalExceptionType;
    if (e.getJasperError() != null) {
      if (e.getJasperError().getHttpStatus() == HttpStatus.NOT_FOUND) {
        functionalExceptionType = FunctionalExceptionType.INVALID_ICCID;
      } else {
        errorMessage =
            String.format(
                "Session provider %s returned error %s : %s",
                SessionProviderEnum.KPN.name(),
                e.getJasperError().getCode(),
                e.getJasperError().getMessage());
        LOGGER.error(errorMessage, e);
        functionalExceptionType = FunctionalExceptionType.SESSION_PROVIDER_ERROR;
      }
    } else {
      errorMessage =
          String.format(
              "Session provider %s returned unknown error message: %s",
              SessionProviderEnum.KPN.name(), e.getMessage());
      LOGGER.error(errorMessage, e);
      functionalExceptionType = FunctionalExceptionType.SESSION_PROVIDER_ERROR;
    }
    throw new FunctionalException(
        functionalExceptionType,
        ComponentType.PROTOCOL_DLMS,
        new OsgpException(ComponentType.PROTOCOL_DLMS, e.getMessage()));
  }
}
