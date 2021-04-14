/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.factories;

import org.opensmartgridplatform.adapter.protocol.dlms.application.config.DevicePingConfig;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ConnectionException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Helper class for acquiring connections to DLMS devices, that takes care of details like
 * initializing invocation counters when required.
 */
@Component
public class DlmsConnectionHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(DlmsConnectionHelper.class);

  private final InvocationCounterManager invocationCounterManager;
  private final DlmsConnectionFactory connectionFactory;
  private final DevicePingConfig devicePingConfig;

  @Autowired
  public DlmsConnectionHelper(
      final InvocationCounterManager invocationCounterManager,
      final DlmsConnectionFactory connectionFactory,
      final DevicePingConfig devicePingConfig) {

    this.invocationCounterManager = invocationCounterManager;
    this.connectionFactory = connectionFactory;
    this.devicePingConfig = devicePingConfig;
  }

  /**
   * Returns an open connection to the device, taking care of details like initializing the
   * invocation counter when required.
   */
  public DlmsConnectionManager createConnectionForDevice(
      final DlmsDevice device, final DlmsMessageListener messageListener) throws OsgpException {

    if (this.devicePingConfig.pingingEnabled() && StringUtils.hasText(device.getIpAddress())) {
      this.devicePingConfig.pinger().ping(device.getIpAddress());
    }

    if (device.needsInvocationCounter() && !device.isInvocationCounterInitialized()) {
      this.invocationCounterManager.initializeInvocationCounter(device);
    }

    try {
      return this.connectionFactory.getConnection(device, messageListener);
    } catch (final ConnectionException e) {
      if (device.needsInvocationCounter() && this.indicatesInvocationCounterOutOfSync(e)) {
        this.resetInvocationCounter(device);
      }
      // Retrow exception, for two reasons:
      // - The error should still be logged, since it can be caused by a problem other than the
      // invocation
      //   counter being out of sync.
      // - This will cause a retry header to be set so the operation will be retried.
      throw e;
    }
  }

  private void resetInvocationCounter(final DlmsDevice device) {
    LOGGER.info(
        "Property invocationCounter of device {} reset, because the ConnectionException logged below could "
            + "result from its current value {} being out of sync with the invocation counter stored on the "
            + "device.",
        device.getDeviceIdentification(),
        device.getInvocationCounter());
    this.invocationCounterManager.resetInvocationCounter(device);
  }

  private boolean indicatesInvocationCounterOutOfSync(final ConnectionException e) {
    return e.getMessage().contains("Socket was closed by remote host.")
        || e.getMessage()
            .contains(
                "Received an association response (AARE) with an error message. Result name REJECTED_PERMANENT. "
                    + "Assumed fault: user.");
  }
}
