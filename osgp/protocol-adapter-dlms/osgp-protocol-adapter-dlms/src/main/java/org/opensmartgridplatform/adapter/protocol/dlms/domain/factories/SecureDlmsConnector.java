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
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.openmuc.jdlms.settings.client.ReferencingMethod;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.InvocationCountingDlmsMessageListener;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

public abstract class SecureDlmsConnector extends Lls0Connector {

  public SecureDlmsConnector(
      final int responseTimeout,
      final int logicalDeviceAddress,
      final DlmsDeviceAssociation deviceAssociation) {
    super(responseTimeout, logicalDeviceAddress, deviceAssociation);
  }

  /**
   * Set the correct security attributes on the tcpConnectionBuilder.
   *
   * @param device The device to connect with.
   * @param tcpConnectionBuilder The connection builder instance.
   */
  protected abstract void setSecurity(
      final DlmsDevice device, final TcpConnectionBuilder tcpConnectionBuilder)
      throws OsgpException;

  /**
   * Create a connection with the device.
   *
   * @param device The device to connect with.
   * @param dlmsMessageListener Listener to set on the connection.
   * @return The connection.
   * @throws IOException When there are problems in connecting to or communicating with the device.
   * @throws OsgpException When there are problems reading the security and authorization keys.
   */
  DlmsConnection createConnection(
      final DlmsDevice device, final DlmsMessageListener dlmsMessageListener)
      throws IOException, OsgpException {

    // Setup connection to device
    final TcpConnectionBuilder tcpConnectionBuilder =
        new TcpConnectionBuilder(InetAddress.getByName(device.getIpAddress()))
            .setResponseTimeout(this.responseTimeout)
            .setLogicalDeviceId(this.logicalDeviceAddress);
    tcpConnectionBuilder
        .setClientId(this.clientId)
        .setReferencingMethod(
            device.isUseSn() ? ReferencingMethod.SHORT : ReferencingMethod.LOGICAL);

    if (device.isUseHdlc()) {
      tcpConnectionBuilder.useHdlc();
    }

    this.setSecurity(device, tcpConnectionBuilder);
    this.setOptionalValues(device, tcpConnectionBuilder);

    if (device.isInDebugMode()
        || dlmsMessageListener instanceof InvocationCountingDlmsMessageListener) {
      tcpConnectionBuilder.setRawMessageListener(dlmsMessageListener);
    }

    return tcpConnectionBuilder.build();
  }
}
