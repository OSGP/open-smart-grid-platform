/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import java.util.function.Consumer;
import org.openmuc.jdlms.DlmsConnection;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.throttling.api.Permit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory that creates open DLMS connections for the duration of executing tasks on the connection,
 * which are passed as consumer argument of the {@link DlmsConnectionManager}.
 */
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
   * Creates an open connection to the device using the appropriate security settings and passes the
   * connection to the {@code taskForConnectionManager} to execute the tasks before closing the
   * connection.
   *
   * <p>This does not use a throttling permit for network access. When such a permit is required,
   * make sure to obtain one that is granted and call {@link
   * #createAndHandleConnection(MessageMetadata, DlmsDevice, DlmsMessageListener, Permit,
   * Consumer)}.
   *
   * @param messageMetadata the metadata of the request message
   * @param device The device to connect to. This reference can be updated when the invalid but
   *     correctable connection credentials are detected.
   * @param dlmsMessageListener A message listener that will be provided to the {@link
   *     DlmsConnection} that is initialized if the given {@code device} is in {@link
   *     DlmsDevice#isInDebugMode() debug mode}. If this is {@code null} no DLMS device
   *     communication debug logging will be done.
   * @param taskForConnectionManager A task for the manager providing access to an open DLMS
   *     connection as well as an optional message listener active in the connection.
   * @throws OsgpException in case of a TechnicalException or FunctionalException
   */
  public void createAndHandleConnection(
      final MessageMetadata messageMetadata,
      final DlmsDevice device,
      final DlmsMessageListener dlmsMessageListener,
      final Consumer<DlmsConnectionManager> taskForConnectionManager)
      throws OsgpException {

    this.createAndHandleConnection(
        messageMetadata, device, dlmsMessageListener, null, taskForConnectionManager);
  }

  /**
   * Creates an open connection to the device using the appropriate security settings and passes the
   * connection to the {@code taskForConnectionManager} to execute the tasks before closing the
   * connection.
   *
   * @param messageMetadata the metadata of the request message
   * @param device The device to connect to. This reference can be updated when the invalid but
   *     correctable connection credentials are detected.
   * @param dlmsMessageListener A message listener that will be provided to the {@link
   *     DlmsConnection} that is initialized if the given {@code device} is in {@link
   *     DlmsDevice#isInDebugMode() debug mode}. If this is {@code null} no DLMS device
   *     communication debug logging will be done.
   * @param permit a permit to access the network, to be released when closing the connection
   * @param taskForConnectionManager A task for the manager providing access to an open DLMS
   *     connection as well as an optional message listener active in the connection.
   * @throws OsgpException in case of a TechnicalException or FunctionalException
   */
  public void createAndHandleConnection(
      final MessageMetadata messageMetadata,
      final DlmsDevice device,
      final DlmsMessageListener dlmsMessageListener,
      final Permit permit,
      final Consumer<DlmsConnectionManager> taskForConnectionManager)
      throws OsgpException {

    this.createAndHandleConnectionWithSecurityLevel(
        messageMetadata,
        device,
        dlmsMessageListener,
        SecurityLevel.forDevice(device),
        permit,
        taskForConnectionManager);
  }

  /**
   * Creates an open connection to the device using its Public client association and passes the
   * connection to the {@code taskForConnectionManager} to execute the tasks before closing the
   * connection.
   *
   * <p>This does not use a throttling permit for network access. When such a permit is required,
   * make sure to obtain one that is granted and call {@link
   * #createAndHandlePublicClientConnection(MessageMetadata, DlmsDevice, DlmsMessageListener, Permit, Consumer).
   *
   * @param messageMetadata the metadata of the request message
   * @param device The device to connect to. This reference can be updated when the invalid but
   *     correctable connection credentials are detected.
   * @param dlmsMessageListener A message listener that will be provided to the {@link
   *     DlmsConnection} that is initialized if the given {@code device} is in {@link
   *     DlmsDevice#isInDebugMode() debug mode}. If this is {@code null} no DLMS device
   *     communication debug logging will be done.
   * @param taskForConnectionManager A task for the DLMS connection manager to handle when the DLMS
   *     connection is open
   * @throws OsgpException in case of a TechnicalException or FunctionalException
   */
  public void createAndHandlePublicClientConnection(
      final MessageMetadata messageMetadata,
      final DlmsDevice device,
      final DlmsMessageListener dlmsMessageListener,
      final Consumer<DlmsConnectionManager> taskForConnectionManager)
      throws OsgpException {

    this.createAndHandlePublicClientConnection(
        messageMetadata, device, dlmsMessageListener, null, taskForConnectionManager);
  }

  /**
   * Creates an open connection to the device using its Public client association and passes the
   * connection to the {@code taskForConnectionManager} to execute the tasks before closing the
   * connection.
   *
   * @param messageMetadata the metadata of the request message
   * @param device The device to connect to. This reference can be updated when the invalid but
   *     correctable connection credentials are detected.
   * @param dlmsMessageListener A message listener that will be provided to the {@link
   *     DlmsConnection} that is initialized if the given {@code device} is in {@link
   *     DlmsDevice#isInDebugMode() debug mode}. If this is {@code null} no DLMS device
   *     communication debug logging will be done.
   * @param permit a permit to access the network, to be released when closing the connection
   * @param taskForConnectionManager A task for the DLMS connection manager to handle when the DLMS
   *     connection is open
   * @throws OsgpException in case of a TechnicalException or FunctionalException
   */
  public void createAndHandlePublicClientConnection(
      final MessageMetadata messageMetadata,
      final DlmsDevice device,
      final DlmsMessageListener dlmsMessageListener,
      final Permit permit,
      final Consumer<DlmsConnectionManager> taskForConnectionManager)
      throws OsgpException {

    this.createAndHandleConnectionWithSecurityLevel(
        messageMetadata,
        device,
        dlmsMessageListener,
        SecurityLevel.LLS0,
        permit,
        taskForConnectionManager);
  }

  private void createAndHandleConnectionWithSecurityLevel(
      final MessageMetadata messageMetadata,
      final DlmsDevice device,
      final DlmsMessageListener dlmsMessageListener,
      final SecurityLevel securityLevel,
      final Permit permit,
      final Consumer<DlmsConnectionManager> taskForConnectionManager)
      throws OsgpException {

    this.domainHelperService.setIpAddressFromMessageMetadataOrSessionProvider(
        device, messageMetadata);

    try (final DlmsConnectionManager connectionManager =
        new DlmsConnectionManager(
            this.connectorFor(securityLevel),
            messageMetadata,
            device,
            dlmsMessageListener,
            this.domainHelperService,
            permit)) {

      connectionManager.connect();
      taskForConnectionManager.accept(connectionManager);
    }
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
