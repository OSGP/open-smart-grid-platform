/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import org.openmuc.jdlms.DlmsConnection;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Factory that returns open DLMS connections. */
@Component
public class DlmsConnectionFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(DlmsConnectionFactory.class);

  private final Hls5Connector hls5Connector;
  private final Lls1Connector lls1Connector;
  private final Lls0Connector lls0Connector;
  private final DomainHelperService domainHelperService;

  @Autowired
  public DlmsConnectionFactory(
      final Hls5Connector hls5Connector,
      final Lls1Connector lls1Connector,
      final Lls0Connector lls0Connector,
      final DomainHelperService domainHelperService) {
    this.hls5Connector = hls5Connector;
    this.lls1Connector = lls1Connector;
    this.lls0Connector = lls0Connector;
    this.domainHelperService = domainHelperService;
  }

  /**
   * Returns an open connection to the device using the appropriate security settings.
   *
   * @param device The device to connect to. This reference can be updated when the invalid but
   *     correctable connection credentials are detected.
   * @param dlmsMessageListener A message listener that will be provided to the {@link
   *     DlmsConnection} that is initialized if the given {@code device} is in {@link
   *     DlmsDevice#isInDebugMode() debug mode}. If this is {@code null} no DLMS device
   *     communication debug logging will be done.
   * @return a manager providing access to an open DLMS connection as well as an optional message
   *     listener active in the connection.
   * @throws OsgpException in case of a TechnicalException or FunctionalException
   */
  public DlmsConnectionManager getConnection(
      final DlmsDevice device, final DlmsMessageListener dlmsMessageListener) throws OsgpException {
    return this.newConnectionWithSecurityLevel(
        device, dlmsMessageListener, SecurityLevel.forDevice(device));
  }

  /**
   * Returns an open connection to the device using its Public client association.
   *
   * @param device The device to connect to. This reference can be updated when the invalid but
   *     correctable connection credentials are detected.
   * @param dlmsMessageListener A message listener that will be provided to the {@link
   *     DlmsConnection} that is initialized if the given {@code device} is in {@link
   *     DlmsDevice#isInDebugMode() debug mode}. If this is {@code null} no DLMS device
   *     communication debug logging will be done.
   * @return a manager providing access to an open DLMS connection as well as an optional message
   *     listener active in the connection.
   * @throws OsgpException in case of a TechnicalException or FunctionalException
   */
  public DlmsConnectionManager getPublicClientConnection(
      final DlmsDevice device, final DlmsMessageListener dlmsMessageListener) throws OsgpException {
    return this.newConnectionWithSecurityLevel(device, dlmsMessageListener, SecurityLevel.LLS0);
  }

  private DlmsConnectionManager newConnectionWithSecurityLevel(
      final DlmsDevice device,
      final DlmsMessageListener dlmsMessageListener,
      final SecurityLevel securityLevel)
      throws OsgpException {
    final DlmsConnectionManager connectionManager =
        new DlmsConnectionManager(
            this.connectorFor(securityLevel),
            device,
            dlmsMessageListener,
            this.domainHelperService);
    connectionManager.connect();
    return connectionManager;
  }

  private DlmsConnector connectorFor(final SecurityLevel securityLevel) throws FunctionalException {
    switch (securityLevel) {
      case HLS5:
        return this.hls5Connector;
      case LLS1:
        return this.lls1Connector;
      case LLS0:
        return this.lls0Connector;
      default:
        LOGGER.error("Only HLS 5, LLS 1 and public (LLS 0) connections are currently supported");
        throw new FunctionalException(
            FunctionalExceptionType.UNSUPPORTED_COMMUNICATION_SETTING, ComponentType.PROTOCOL_DLMS);
    }
  }
}
