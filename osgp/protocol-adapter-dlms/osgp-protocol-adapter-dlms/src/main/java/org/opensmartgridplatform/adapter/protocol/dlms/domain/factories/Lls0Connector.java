// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import static org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics.METRIC_REQUEST_TIMER_PREFIX;
import static org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics.TAG_BTS_ID;
import static org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics.TAG_CELL_ID;
import static org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics.TAG_COMMUNICATION_METHOD;

import io.micrometer.core.instrument.Timer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.openmuc.jdlms.settings.client.ReferencingMethod;
import org.opensmartgridplatform.adapter.protocol.dlms.application.metrics.ProtocolAdapterMetrics;
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

  private static final String TIMER_NAME = "create_connection";

  protected final int responseTimeout;

  protected final int logicalDeviceAddress;

  protected final int clientId;

  protected ProtocolAdapterMetrics protocolAdapterMetrics;

  public Lls0Connector(
      final int responseTimeout,
      final int logicalDeviceAddress,
      final DlmsDeviceAssociation deviceAssociation,
      final ProtocolAdapterMetrics protocolAdapterMetrics) {
    this.responseTimeout = responseTimeout;
    this.logicalDeviceAddress = logicalDeviceAddress;
    this.clientId = deviceAssociation.getClientId();
    this.protocolAdapterMetrics = protocolAdapterMetrics;
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
      final Timer timer = this.createTimer(device, messageMetadata);
      final long starttime = System.currentTimeMillis();

      final DlmsConnection dlmsConnection = tcpConnectionBuilder.build();

      this.recordTimer(timer, starttime);

      return dlmsConnection;
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

  protected Timer createTimer(final DlmsDevice device, final MessageMetadata messageMetadata) {
    final Map<String, String> tags = new HashMap<>();
    tags.put(TAG_COMMUNICATION_METHOD, String.valueOf(device.getCommunicationMethod()));
    if (messageMetadata.getBaseTransceiverStationId() != null) {
      tags.put(TAG_BTS_ID, String.valueOf(messageMetadata.getBaseTransceiverStationId()));
    }
    if (messageMetadata.getCellId() != null) {
      tags.put(TAG_CELL_ID, String.valueOf(messageMetadata.getCellId()));
    }
    return this.protocolAdapterMetrics.createTimer(METRIC_REQUEST_TIMER_PREFIX + TIMER_NAME, tags);
  }

  void recordTimer(final Timer timer, final long starttime) {
    this.protocolAdapterMetrics.recordTimer(
        timer, System.currentTimeMillis() - starttime, TimeUnit.MILLISECONDS);
  }
}
