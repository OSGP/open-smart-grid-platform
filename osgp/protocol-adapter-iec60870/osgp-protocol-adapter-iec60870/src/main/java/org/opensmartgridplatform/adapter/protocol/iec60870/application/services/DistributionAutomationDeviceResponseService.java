/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.MeasurementReportingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistributionAutomationDeviceResponseService extends AbstractDeviceResponseService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DistributionAutomationDeviceResponseService.class);

  private static final DeviceType DEVICE_TYPE = DeviceType.DISTRIBUTION_AUTOMATION_DEVICE;

  @Autowired private MeasurementReportingService measurementReportingService;

  public DistributionAutomationDeviceResponseService() {
    super(DEVICE_TYPE);
  }

  @Override
  public void process(
      final MeasurementReportDto measurementReportDto, final ResponseMetadata responseMetadata) {
    LOGGER.info(
        "Received measurement report {} for distribution automation device {}.",
        measurementReportDto,
        responseMetadata.getDeviceIdentification());

    this.measurementReportingService.send(measurementReportDto, responseMetadata);
  }
}
