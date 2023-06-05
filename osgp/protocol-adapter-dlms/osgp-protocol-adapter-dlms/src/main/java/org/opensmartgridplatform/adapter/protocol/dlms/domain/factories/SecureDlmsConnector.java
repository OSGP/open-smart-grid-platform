// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

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
   * @param messageMetadata the metadata of the request message
   * @param device The device to connect with.
   * @param tcpConnectionBuilder The connection builder instance.
   */
  protected abstract void setSecurity(
      final MessageMetadata messageMetadata,
      final DlmsDevice device,
      final SecurityKeyProvider keyProvider,
      final TcpConnectionBuilder tcpConnectionBuilder)
      throws OsgpException;

  /**
   * Create a connection with the device.
   *
   * @param messageMetadata the metadata of the request message
   * @param device The device to connect with.
   * @param dlmsMessageListener Listener to set on the connection.
   * @return The connection.
   * @throws IOException When there are problems in connecting to or communicating with the device.
   * @throws OsgpException When there are problems reading the security and authorization keys.
   */
  DlmsConnection createConnection(
      final MessageMetadata messageMetadata,
      final DlmsDevice device,
      final DlmsMessageListener dlmsMessageListener,
      final SecurityKeyProvider keyProvider)
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

    this.setSecurity(messageMetadata, device, keyProvider, tcpConnectionBuilder);
    this.setOptionalValues(device, tcpConnectionBuilder);

    if (device.isInDebugMode()
        || dlmsMessageListener instanceof InvocationCountingDlmsMessageListener) {
      tcpConnectionBuilder.setRawMessageListener(dlmsMessageListener);
    }

    return tcpConnectionBuilder.build();
  }
}
