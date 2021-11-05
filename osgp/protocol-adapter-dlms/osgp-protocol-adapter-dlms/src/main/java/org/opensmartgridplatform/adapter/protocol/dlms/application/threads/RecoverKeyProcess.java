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
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openmuc.jdlms.DlmsConnection;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.DomainHelperService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.SecretManagementService;
import org.opensmartgridplatform.adapter.protocol.dlms.application.services.ThrottlingService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.Hls5Connector;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.RecoverKeyException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.InvocationCountingDlmsMessageListener;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@Slf4j
public class RecoverKeyProcess implements Runnable {

  private final DomainHelperService domainHelperService;

  @Setter private String deviceIdentification;

  @Setter private String ipAddress;

  @Setter private MessageMetadata messageMetadata;

  private final Hls5Connector hls5Connector;

  private final SecretManagementService secretManagementService;

  private final ThrottlingService throttlingService;

  private final DlmsDeviceRepository deviceRepository;

  public RecoverKeyProcess(
      final DomainHelperService domainHelperService,
      final Hls5Connector hls5Connector,
      final SecretManagementService secretManagementService,
      final ThrottlingService throttlingService,
      final DlmsDeviceRepository deviceRepository) {
    this.domainHelperService = domainHelperService;
    this.hls5Connector = hls5Connector;
    this.secretManagementService = secretManagementService;
    this.throttlingService = throttlingService;
    this.deviceRepository = deviceRepository;
  }

  @Override
  public void run() {
    this.checkState();

    log.info(
        "[{}] Attempting key recovery for device {}",
        this.messageMetadata.getCorrelationUid(),
        this.deviceIdentification);

    final DlmsDevice device = this.findDevice();

    if (!this.canConnectUsingNewKeys(device)) {
      log.warn(
          "[{}] Could not recover keys: could not connect to device {} using New keys",
          this.messageMetadata.getCorrelationUid(),
          this.deviceIdentification);
      return;
    }

    try {
      this.secretManagementService.activateNewKeys(
          this.messageMetadata,
          this.deviceIdentification,
          Arrays.asList(E_METER_ENCRYPTION, E_METER_AUTHENTICATION));
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
    InvocationCountingDlmsMessageListener dlmsMessageListener = null;
    try {
      this.throttlingService.openConnection();

      if (device.needsInvocationCounter()) {
        dlmsMessageListener = new InvocationCountingDlmsMessageListener();
      }

      connection =
          this.hls5Connector.connectUnchecked(
              this.messageMetadata,
              device,
              dlmsMessageListener,
              this.secretManagementService::getNewKeys);
      return connection != null;
    } catch (final Exception e) {
      log.warn(
          "Connection exception during key recovery process for device: {} {}",
          device.getDeviceIdentification(),
          e.getMessage(),
          e);
      return false;
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (final IOException e) {
          log.warn("Closing connection exception: {}", e.getMessage(), e);
        }
      }

      this.throttlingService.closeConnection();

      if (dlmsMessageListener != null) {
        final int numberOfSentMessages = dlmsMessageListener.getNumberOfSentMessages();
        device.incrementInvocationCounter(numberOfSentMessages);
        this.deviceRepository.save(device);
      }
    }
  }
}
