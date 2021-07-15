/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.threads;

import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_AUTHENTICATION;
import static org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType.E_METER_ENCRYPTION;

import java.io.IOException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.DlmsConnection;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecretManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ThrottlingService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.Hls5Connector;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.RecoverKeyException;

@Slf4j
public class RecoverKeyProcess implements Runnable {

  private final DomainHelperService domainHelperService;

  private String deviceIdentification;

  private String ipAddress;

  private final Hls5Connector hls5Connector;

  private final SecretManagementService secretManagementService;

  private final ThrottlingService throttlingService;

  public RecoverKeyProcess(
      final DomainHelperService domainHelperService,
      final Hls5Connector hls5Connector,
      final SecretManagementService secretManagementService,
      final ThrottlingService throttlingService) {
    this.domainHelperService = domainHelperService;
    this.hls5Connector = hls5Connector;
    this.secretManagementService = secretManagementService;
    this.throttlingService = throttlingService;
  }

  public void setDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public void setIpAddress(final String ipAddress) {
    this.ipAddress = ipAddress;
  }

  @Override
  public void run() {
    this.checkState();

    log.info("Attempting key recovery for device {}", this.deviceIdentification);

    final DlmsDevice device = this.findDevice();

    if (!this.secretManagementService.hasNewSecretOfType(
        this.deviceIdentification, E_METER_AUTHENTICATION)) {
      log.error(
          "Could not recover keys: device has no new authorisation key registered in secret-mgmt module");
      return;
    }
    if (!this.canConnectUsingNewKeys(device)) {
      log.error("Could not recover keys: could not connect to device using new keys");
      return;
    }

    try {
      this.secretManagementService.activateNewKeys(
          this.deviceIdentification, Arrays.asList(E_METER_ENCRYPTION, E_METER_AUTHENTICATION));
    } catch (final Exception e) {
      throw new RecoverKeyException(e);
    }
  }

  private DlmsDevice findDevice() {
    try {
      return this.domainHelperService.findDlmsDevice(this.deviceIdentification, this.ipAddress);
    } catch (final Exception e) {
      throw new RecoverKeyException(e);
    }
  }

  private void checkState() {
    if (this.deviceIdentification == null) {
      throw new IllegalStateException("DeviceIdentification not set.");
    }
    if (this.ipAddress == null) {
      throw new IllegalStateException("IP address not set.");
    }
  }

  private boolean canConnectUsingNewKeys(final DlmsDevice device) {
    DlmsConnection connection = null;
    try {
      this.throttlingService.openConnection();

      connection =
          this.hls5Connector.connectUnchecked(
              device, null, this.secretManagementService::getNewKeys);
      return connection != null;
    } catch (final Exception e) {
      log.error("Connection exception: {}", e.getMessage(), e);
      return false;
    } finally {
      this.throttlingService.closeConnection();

      if (connection != null) {
        try {
          connection.close();
        } catch (final IOException e) {
          log.warn("Connection exception: {}", e.getMessage(), e);
        }
      }
    }
  }
}
