/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.openmuc.jdlms.settings.client.ReferencingMethod;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lls0Connector extends DlmsConnector {

  private static final Logger LOGGER = LoggerFactory.getLogger(Lls0Connector.class);

  protected final int responseTimeout;

  protected final int logicalDeviceAddress;

  protected final int clientId;

  public Lls0Connector(
      final int responseTimeout,
      final int logicalDeviceAddress,
      final DlmsDeviceAssociation deviceAssociation) {
    this.responseTimeout = responseTimeout;
    this.logicalDeviceAddress = logicalDeviceAddress;
    this.clientId = deviceAssociation.getClientId();
  }

  @Override
  public DlmsConnection connect(
      final MessageMetadata messageMetadata,
      final DlmsDevice device,
      final DlmsMessageListener dlmsMessageListener)
      throws OsgpException {

    // Make sure neither device or device.getIpAddress() is null.
    this.checkDevice(device);
    this.checkIpAddress(device);

    // Setup connection to device
    final TcpConnectionBuilder tcpConnectionBuilder;
    try {

      tcpConnectionBuilder =
          new TcpConnectionBuilder(InetAddress.getByName(device.getIpAddress()))
              .setResponseTimeout(this.responseTimeout)
              .setLogicalDeviceId(this.logicalDeviceAddress)
              .setClientId(this.clientId)
              .setReferencingMethod(
                  device.isUseSn() ? ReferencingMethod.SHORT : ReferencingMethod.LOGICAL);

      if (device.isUseHdlc()) {
        tcpConnectionBuilder.useHdlc();
      }
    } catch (final UnknownHostException e) {
      LOGGER.error("The IP address is not found: {}", device.getIpAddress(), e);
      // Unknown IP, unrecoverable.
      throw new TechnicalException(
          ComponentType.PROTOCOL_DLMS, "The IP address is not found: " + device.getIpAddress());
    }

    this.setOptionalValues(device, tcpConnectionBuilder);

    if (device.isInDebugMode()) {
      tcpConnectionBuilder.setRawMessageListener(dlmsMessageListener);
    }

    try {
      return tcpConnectionBuilder.build();
    } catch (final IOException e) {
      final String msg =
          String.format(
              "Error creating connection for device %s with Ip address:%s Port:%d UseHdlc:%b UseSn:%b Message:%s",
              device.getDeviceIdentification(),
              device.getIpAddress(),
              device.getPort(),
              device.isUseHdlc(),
              device.isUseSn(),
              e.getMessage());
      LOGGER.error(msg);
      throw new ConnectionException(msg, e);
    }
  }
}
