// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders;

import java.util.Optional;
import javax.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessSmsClient;
import org.opensmartgridplatform.adapter.protocol.jasper.exceptions.OsgpJasperException;
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
  private final DeviceSessionService deviceSessionService;

  public SessionProviderKpnPushAlarm(
      final SessionProviderMap sessionProviderMap,
      final JasperWirelessSmsClient jasperWirelessSmsClient,
      final DeviceSessionService deviceSessionService) {
    super(sessionProviderMap);
    this.jasperWirelessSmsClient = jasperWirelessSmsClient;
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

      return this.waitForIpAddress(deviceIdentification);
    } catch (final OsgpJasperException e) {
      throw new FunctionalException(
          FunctionalExceptionType.INVALID_ICCID, ComponentType.PROTOCOL_DLMS, e);
    }
  }

  private Optional<String> waitForIpAddress(final String deviceIdentification) {
    LOGGER.info(
        "Wait for ip-address, this will be pushed by alarm for device: {}", deviceIdentification);
    final Optional<String> ipAddress =
        this.deviceSessionService.waitForIpAddress(deviceIdentification);
    LOGGER.info("Received ip-address: {} for device: {}", ipAddress, deviceIdentification);
    return ipAddress;
  }
}
