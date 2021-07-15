/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import javax.annotation.PostConstruct;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.DeviceResponseService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.DeviceResponseServiceRegistry;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDeviceResponseService implements DeviceResponseService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDeviceResponseService.class);

  @Autowired private DeviceResponseServiceRegistry deviceResponseServiceRegistry;

  private final DeviceType deviceType;

  protected AbstractDeviceResponseService(final DeviceType deviceType) {
    this.deviceType = deviceType;
  }

  @PostConstruct
  private void registerService() {
    LOGGER.info("Registering device response service for device type {}", this.deviceType);
    this.deviceResponseServiceRegistry.register(this.deviceType, this);
  }

  @Override
  public void processEvent(
      final MeasurementReportDto measurementReportDto, final ResponseMetadata responseMetadata) {
    LOGGER.info(
        "Processing a report as event is not implemented for device type {}, measurement report {} for device {}",
        this.deviceType,
        measurementReportDto,
        responseMetadata.getDeviceIdentification());
  }
}
