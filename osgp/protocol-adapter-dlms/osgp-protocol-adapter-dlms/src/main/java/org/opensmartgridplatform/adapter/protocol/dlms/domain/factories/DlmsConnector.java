// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import org.apache.commons.lang3.StringUtils;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DlmsConnector {
  private static final Logger LOGGER = LoggerFactory.getLogger(DlmsConnector.class);

  public abstract DlmsConnection connect(
      final MessageMetadata messageMetadata,
      final DlmsDevice device,
      final DlmsMessageListener dlmsMessageListener)
      throws OsgpException;

  protected void checkDevice(final DlmsDevice device) {
    if (device == null) {
      throw new IllegalStateException("Can not connect because no device is set.");
    }
  }

  protected void checkIpAddress(final DlmsDevice device) throws FunctionalException {
    if (StringUtils.isBlank(device.getIpAddress())) {
      final String errorMessage =
          String.format(
              "Unable to get connection for device %s, because the IP address:%s is not valid or empty",
              device.getDeviceIdentification(), device.getIpAddress());
      LOGGER.error(errorMessage);

      throw new FunctionalException(
          FunctionalExceptionType.INVALID_IP_ADDRESS, ComponentType.PROTOCOL_DLMS);
    }
  }

  protected void setOptionalValues(
      final DlmsDevice device, final TcpConnectionBuilder tcpConnectionBuilder)
      throws FunctionalException {
    if (device.getPort() != null) {
      tcpConnectionBuilder.setPort(device.getPort().intValue());
    }
    if (device.getLogicalId() != null) {
      tcpConnectionBuilder.setLogicalDeviceId(device.getLogicalId().intValue());
    }

    final Integer challengeLength = device.getChallengeLength();

    try {
      if (challengeLength != null) {
        tcpConnectionBuilder.setChallengeLength(challengeLength);
      }
    } catch (final IllegalArgumentException e) {
      final String errorMessage =
          String.format(
              "Challenge length has to be between 8 and 64 for device %s",
              device.getDeviceIdentification());
      LOGGER.error(errorMessage);

      throw new FunctionalException(
          FunctionalExceptionType.CHALLENGE_LENGTH_OUT_OF_RANGE, ComponentType.PROTOCOL_DLMS, e);
    }
  }
}
