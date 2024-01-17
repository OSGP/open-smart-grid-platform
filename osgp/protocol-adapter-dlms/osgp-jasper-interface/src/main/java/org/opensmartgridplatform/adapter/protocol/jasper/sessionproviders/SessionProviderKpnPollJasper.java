// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders;

import java.util.Optional;
import jakarta.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessSmsClient;
import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessTerminalClient;
import org.opensmartgridplatform.adapter.protocol.jasper.response.GetSessionInfoResponse;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.exceptions.SessionProviderException;
import org.opensmartgridplatform.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class SessionProviderKpnPollJasper extends SessionProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(SessionProviderKpnPollJasper.class);

  private final JasperWirelessTerminalClient jasperWirelessTerminalClient;
  private final JasperWirelessSmsClient jasperWirelessSmsClient;
  private final int jasperGetSessionRetries;

  private final int jasperGetSessionSleepBetweenRetries;

  public SessionProviderKpnPollJasper(
      final SessionProviderMap sessionProviderMap,
      final JasperWirelessTerminalClient jasperWirelessTerminalClient,
      final JasperWirelessSmsClient jasperWirelessSmsClient,
      final int jasperGetSessionRetries,
      final int jasperGetSessionSleepBetweenRetries) {
    super(sessionProviderMap);
    this.jasperWirelessTerminalClient = jasperWirelessTerminalClient;
    this.jasperWirelessSmsClient = jasperWirelessSmsClient;
    this.jasperGetSessionRetries = jasperGetSessionRetries;
    this.jasperGetSessionSleepBetweenRetries = jasperGetSessionSleepBetweenRetries;
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
    String deviceIpAddress;
    try {
      deviceIpAddress = this.getIpAddressFromSessionInfo(iccId);
      if (deviceIpAddress != null) {
        return Optional.of(deviceIpAddress);
      }

      // If the result is null then the meter is not in session (not
      // awake).
      // So wake up the meter and start polling for the session
      this.jasperWirelessSmsClient.sendWakeUpSMS(iccId);
      deviceIpAddress = this.pollForSession(iccId);

    } catch (final OsgpJasperException e) {
      throw new FunctionalException(
          FunctionalExceptionType.INVALID_ICCID, ComponentType.PROTOCOL_DLMS, e);
    }
    return Optional.ofNullable(deviceIpAddress);
  }

  private String pollForSession(final String iccId) throws OsgpException {

    String deviceIpAddress = null;
    try {
      for (int i = 0; i < this.jasperGetSessionRetries; i++) {
        Thread.sleep(this.jasperGetSessionSleepBetweenRetries);
        deviceIpAddress = this.getIpAddressFromSessionInfo(iccId);
        if (deviceIpAddress != null) {
          return deviceIpAddress;
        }
      }
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.error(
          "Interrupted while sleeping before calling the sessionProvider.getIpAddress [iccId: "
              + iccId
              + "]",
          e);
      throw new FunctionalException(
          FunctionalExceptionType.SESSION_PROVIDER_ERROR, ComponentType.PROTOCOL_DLMS, e);
    } catch (final SessionProviderException e) {
      LOGGER.error("SessionProviderException occurred [iccId: " + iccId + "]", e);
      throw new FunctionalException(
          FunctionalExceptionType.SESSION_PROVIDER_ERROR, ComponentType.PROTOCOL_DLMS, e);
    }
    return deviceIpAddress;
  }

  private String getIpAddressFromSessionInfo(final String iccId) throws OsgpException {
    try {
      final GetSessionInfoResponse response = this.jasperWirelessTerminalClient.getSession(iccId);
      return response.getIpAddress();
    } catch (final OsgpJasperException e) {
      this.handleException(e);
      return null;
    }
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
