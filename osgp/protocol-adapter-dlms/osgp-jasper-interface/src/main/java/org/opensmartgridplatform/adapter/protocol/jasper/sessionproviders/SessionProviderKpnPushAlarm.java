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

public class SessionProviderKpnPushAlarm extends SessionProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(SessionProviderKpnPushAlarm.class);

  private final JasperWirelessSmsClient jasperWirelessSmsClient;
  private final JasperWirelessTerminalClient jasperWirelessTerminalClient;

  private final DeviceSessionService deviceSessionService;

  public SessionProviderKpnPushAlarm(
      final SessionProviderMap sessionProviderMap,
      final JasperWirelessSmsClient jasperWirelessSmsClient,
      final JasperWirelessTerminalClient jasperWirelessTerminalClient,
      final DeviceSessionService deviceSessionService) {
    super(sessionProviderMap);
    this.jasperWirelessSmsClient = jasperWirelessSmsClient;
    this.jasperWirelessTerminalClient = jasperWirelessTerminalClient;
    this.deviceSessionService = deviceSessionService;
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
      LOGGER.error(
          "Jasper exception occurred (code: {}, httpStatus: {}): {}",
          e.getJasperError().getCode(),
          e.getJasperError().getHttpStatus(),
          e.getJasperError().getMessage(),
          e);
      throw new FunctionalException(
          FunctionalExceptionType.INVALID_ICCID, ComponentType.PROTOCOL_DLMS, e);
    }
  }

  private Optional<String> waitForIpAddress(final String deviceIdentification, final String iccId)
      throws OsgpJasperException {
    LOGGER.info(
        "Wait for ip-address, this will be pushed by alarm for device: {}", deviceIdentification);
    Optional<String> ipAddress = this.deviceSessionService.waitForIpAddress(deviceIdentification);
    if (ipAddress.isEmpty()) {
      LOGGER.info(
          "Did not receive an ip-address for device: {}, try to get ip-address from session provider",
          deviceIdentification);
      ipAddress = this.getIpAddressFromSessionInfo(deviceIdentification, iccId);
    }
    ipAddress.ifPresent(
        s -> LOGGER.info("Received ip-address: {} for device: {}", s, deviceIdentification));

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
}
