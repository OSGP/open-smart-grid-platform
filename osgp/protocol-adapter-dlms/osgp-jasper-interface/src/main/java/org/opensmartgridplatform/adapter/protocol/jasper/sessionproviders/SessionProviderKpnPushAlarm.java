// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders;

import java.util.Optional;
import javax.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessSmsClient;
import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessTerminalClient;
import org.opensmartgridplatform.adapter.protocol.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.adapter.protocol.jasper.response.GetSessionInfoResponse;
import org.opensmartgridplatform.adapter.protocol.jasper.service.DeviceSessionService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class SessionProviderKpnPushAlarm extends SessionProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(SessionProviderKpnPushAlarm.class);

  private final JasperWirelessSmsClient jasperWirelessSmsClient;
  private final JasperWirelessTerminalClient jasperWirelessTerminalClient;
  private final DeviceSessionService deviceSessionService;
  private final int nrOfAttempts;

  public SessionProviderKpnPushAlarm(
      final SessionProviderMap sessionProviderMap,
      final JasperWirelessSmsClient jasperWirelessSmsClient,
      final JasperWirelessTerminalClient jasperWirelessTerminalClient,
      final DeviceSessionService deviceSessionService,
      final int nrOfAttempts) {
    super(sessionProviderMap);
    this.jasperWirelessSmsClient = jasperWirelessSmsClient;
    this.jasperWirelessTerminalClient = jasperWirelessTerminalClient;
    this.deviceSessionService = deviceSessionService;
    this.nrOfAttempts = nrOfAttempts;
  }

  /**
   * Initialization function executed after dependency injection has finished. The SessionProvider
   * Singleton is added to the HashMap of SessionProviderMap.
   */
  @PostConstruct
  public void init() {
    this.sessionProviderMap.addProvider(SessionProviderEnum.KPN, this);
  }

  @Override
  public Optional<String> getIpAddress(final String deviceIdentification, final String iccId)
      throws OsgpException {
    LOGGER.info("Get ip address for device: " + deviceIdentification);
    try {
      this.jasperWirelessSmsClient.sendWakeUpSMS(iccId);

      return this.waitForIpAddress(deviceIdentification, iccId);
    } catch (final OsgpJasperException e) {
      this.handleException(e);
      return Optional.empty();
    }
  }

  private Optional<String> waitForIpAddress(final String deviceIdentification, final String iccId)
      throws OsgpJasperException {

    Optional<String> ipAddress = Optional.empty();
    for (int attempt = 1; attempt <= this.nrOfAttempts; attempt++) {
      LOGGER.info(
          "Wait for ip-address, this will be pushed by alarm for device: {}, attempt {}",
          deviceIdentification,
          attempt);
      ipAddress = this.deviceSessionService.waitForIpAddress(deviceIdentification);
      if (ipAddress.isEmpty()) {
        LOGGER.info(
            "Did not receive an ip-address for device: {}, try to get ip-address from session provider, attempt {}",
            deviceIdentification,
            attempt);
        ipAddress = this.getIpAddressFromSessionInfo(deviceIdentification, iccId);
      }
      if (ipAddress.isPresent()) {
        LOGGER.info(
            "Received ip-address: {} for device: {}, attempt: {}",
            ipAddress.get(),
            deviceIdentification,
            attempt);
        return ipAddress;
      }
    }

    return ipAddress;
  }

  private Optional<String> getIpAddressFromSessionInfo(
      final String deviceIdentification, final String iccId) throws OsgpJasperException {
    final GetSessionInfoResponse response = this.jasperWirelessTerminalClient.getSession(iccId);
    final Optional<String> ipAddress = Optional.ofNullable(response.getIpAddress());
    if (ipAddress.isEmpty()) {
      LOGGER.info(
          "Session provider did not return an ip-address for device: {}, icc: {}",
          deviceIdentification,
          iccId);
    } else {
      LOGGER.info(
          "Session provider returned ip-address: {} for device: {}, icc: {}",
          ipAddress.get(),
          deviceIdentification,
          iccId);
    }

    return ipAddress;
  }

  private void handleException(final OsgpJasperException e) throws FunctionalException {
    String errorMessage = "";
    final FunctionalExceptionType functionalExceptionType;
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
