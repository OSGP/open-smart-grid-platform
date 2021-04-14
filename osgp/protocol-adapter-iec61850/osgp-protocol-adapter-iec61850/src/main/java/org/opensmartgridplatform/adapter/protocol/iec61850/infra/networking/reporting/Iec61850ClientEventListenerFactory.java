/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting;

import org.opensmartgridplatform.adapter.protocol.iec61850.application.services.DeviceManagementService;
import org.opensmartgridplatform.adapter.protocol.iec61850.application.services.ReportingService;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.IED;
import org.opensmartgridplatform.adapter.protocol.iec61850.services.DeviceMessageLoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class Iec61850ClientEventListenerFactory {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(Iec61850ClientEventListenerFactory.class);

  @Autowired private DeviceManagementService deviceManagementService;

  @Autowired
  @Qualifier(value = "protocolIec61850DeviceMessageLoggingService")
  private DeviceMessageLoggingService deviceMessageLoggingService;

  @Autowired private ReportingService reportingService;

  public Iec61850ClientBaseEventListener getEventListener(
      final IED ied, final String deviceIdentification, final String organizationIdentification)
      throws ProtocolAdapterException {
    switch (ied) {
      case FLEX_OVL:
        return new Iec61850ClientSSLDEventListener(
            organizationIdentification,
            deviceIdentification,
            this.deviceManagementService,
            this.deviceMessageLoggingService);
      case ABB_RTU:
        return new Iec61850ClientLMDEventListener(
            deviceIdentification, this.deviceManagementService);
      case ZOWN_RTU:
        return new Iec61850ClientRTUEventListener(
            deviceIdentification, this.deviceManagementService, this.reportingService);
      case DA_RTU:
        return new Iec61850ClientDaRTUEventListener(
            deviceIdentification, this.deviceManagementService);
      default:
        LOGGER.warn(
            "Unknown IED {}, could not create event listener for device {}",
            ied,
            deviceIdentification);
        return null;
    }
  }
}
