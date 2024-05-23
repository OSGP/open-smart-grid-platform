// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders;

import jakarta.annotation.PostConstruct;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessSmsClient;
import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessTerminalClient;
import org.opensmartgridplatform.adapter.protocol.jasper.response.GetSessionInfoResponse;
import org.opensmartgridplatform.adapter.protocol.jasper.service.DeviceSessionService;
import org.opensmartgridplatform.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.springframework.http.HttpStatus;

@Slf4j
public class SessionProviderKpnPushAlarm extends SessionProvider {

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
    log.info("Get ip address for device: {}", deviceIdentification);
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
      log.info(
          "Wait for ip-address, this will be pushed by alarm for device: {}, attempt {}",
          deviceIdentification,
          attempt);
      ipAddress = this.deviceSessionService.waitForIpAddress(deviceIdentification);
      if (ipAddress.isEmpty()) {
        log.info(
            "Did not receive an ip-address for device: {}, try to get ip-address from session provider, attempt {}",
            deviceIdentification,
            attempt);
        ipAddress = this.getIpAddressFromSessionInfo(deviceIdentification, iccId);
      }
      if (ipAddress.isPresent()) {
        log.info(
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
      log.info(
          "Session provider did not return an ip-address for device: {}, icc: {}",
          deviceIdentification,
          iccId);
    } else {
      log.info(
          "Session provider returned ip-address: {} for device: {}, icc: {}",
          ipAddress.get(),
          deviceIdentification,
          iccId);
    }

    return ipAddress;
  }

  private void handleException(final OsgpJasperException e) throws FunctionalException {
    final FunctionalExceptionType functionalExceptionType;
    if (e.getJasperError() != null) {
      if (e.getJasperError().getHttpStatus() == HttpStatus.NOT_FOUND) {
        functionalExceptionType = FunctionalExceptionType.INVALID_ICCID;
      } else {
        log.error(
            "Session provider {} returned error {} : {}",
            SessionProviderEnum.KPN.name(),
            e.getJasperError().getCode(),
            e.getJasperError().getMessage(),
            e);
        functionalExceptionType = FunctionalExceptionType.SESSION_PROVIDER_ERROR;
      }
    } else {
      log.error(
          "Session provider {} returned unknown error message: {}",
          SessionProviderEnum.KPN.name(),
          e.getMessage(),
          e);
      functionalExceptionType = FunctionalExceptionType.SESSION_PROVIDER_ERROR;
    }
    throw new FunctionalException(
        functionalExceptionType,
        ComponentType.PROTOCOL_DLMS,
        new OsgpException(ComponentType.PROTOCOL_DLMS, e.getMessage()));
  }
}
