// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import static org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType.AUTHENTICATION_ERROR;
import static org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType.AUTHENTICATION_REQUIRED;
import static org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType.CONNECTION_ERROR;
import static org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType.CONNECTION_ESTABLISH_ERROR;
import static org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType.CONNECTION_REFUSED;
import static org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType.CONNECTION_RESET;
import static org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType.CONNECTION_TIMED_OUT;
import static org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType.ILLEGAL_RESPONSE;
import static org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType.RESPONSE_TIMEOUT;
import static org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType.SOCKET_CLOSED_BY_REMOTE;
import static org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType.UNABLE_TO_DECYPHER;
import static org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType.UNKNOWN;
import static org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType.UNKNOWN_ASSOCIATION_RESULT;
import static org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType.WRAPPER_HEADER_ERROR;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
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

  private static final Map<String, FunctionalExceptionType> errorMap = new HashMap<>();

  static {
    errorMap.put("AUTHENTICATION_ERROR", AUTHENTICATION_ERROR);
    errorMap.put("AUTHENTICATION_REQUIRED", AUTHENTICATION_REQUIRED);
    errorMap.put("Connection refused", CONNECTION_REFUSED);
    errorMap.put("Connection reset", CONNECTION_RESET);
    errorMap.put("Connection timed out", CONNECTION_TIMED_OUT);
    errorMap.put("CONNECTION_ESTABLISH_ERROR", CONNECTION_ESTABLISH_ERROR);
    errorMap.put("Socket was closed by remote host.", SOCKET_CLOSED_BY_REMOTE);
    errorMap.put("Unable to decypher/decrypt xDLMS pdu", UNABLE_TO_DECYPHER);
    errorMap.put("WRAPPER_HEADER_INVALID_VERSION", WRAPPER_HEADER_ERROR);
    errorMap.put("WRAPPER_HEADER_INVALID_SRC_DEST_ADDR", WRAPPER_HEADER_ERROR);
    errorMap.put("WRAPPER_HEADER_INVALID_PAYLOAD_LENGTH", WRAPPER_HEADER_ERROR);
    errorMap.put("WRAPPER_HEADER_INVALID", WRAPPER_HEADER_ERROR);
    errorMap.put("ILLEGAL_RESPONSE", ILLEGAL_RESPONSE);
    errorMap.put("RESPONSE_TIMEOUT", RESPONSE_TIMEOUT);
    errorMap.put("UNKNOWN_ASSOCIATION_RESULT", UNKNOWN_ASSOCIATION_RESULT);
    errorMap.put("UNKNOWN", UNKNOWN);
  }

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

  protected ConnectionException getExceptionWithExceptionType(
      final DlmsDevice device, final Exception e) {
    final String errorMessage = e.getMessage();

    FunctionalExceptionType exceptionType = CONNECTION_ERROR;

    if (errorMessage != null) {
      exceptionType =
          errorMap.entrySet().stream()
              .filter(entry -> errorMessage.contains(entry.getKey()))
              .findFirst()
              .map(Entry::getValue)
              .orElse(CONNECTION_ERROR);
    }

    final String msg =
        String.format(
            "Connection error for device %s with Ip address:%s Port:%d UseHdlc:%b ExceptionType:%s Message:%s",
            device.getDeviceIdentification(),
            device.getIpAddress(),
            device.getPort(),
            device.isUseHdlc(),
            exceptionType.name(),
            e.getMessage());
    LOGGER.error(msg);

    return new ConnectionException(msg, e, exceptionType);
  }
}
