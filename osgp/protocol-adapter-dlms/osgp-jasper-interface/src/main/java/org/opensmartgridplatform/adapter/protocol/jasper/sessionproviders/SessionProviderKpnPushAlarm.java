// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders;

import java.util.Optional;
import javax.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessSmsClient;
import org.opensmartgridplatform.adapter.protocol.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionProviderKpnPushAlarm extends SessionProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(SessionProviderKpnPushAlarm.class);

  private final JasperWirelessSmsClient jasperWirelessSmsClient;

  public SessionProviderKpnPushAlarm(
      final SessionProviderMap sessionProviderMap,
      final JasperWirelessSmsClient jasperWirelessSmsClient) {
    super(sessionProviderMap);
    this.jasperWirelessSmsClient = jasperWirelessSmsClient;
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
      this.clearIpAddress(deviceIdentification);

      this.jasperWirelessSmsClient.sendWakeUpSMS(iccId);

      return this.waitForIpAddress(deviceIdentification);
    } catch (final OsgpJasperException e) {
      throw new FunctionalException(
          FunctionalExceptionType.INVALID_ICCID, ComponentType.PROTOCOL_DLMS, e);
    }
  }

  private void clearIpAddress(final String deviceIdentification) {}

  private Optional<String> waitForIpAddress(final String deviceIdentification) {
    return Optional.empty();
  }
}
