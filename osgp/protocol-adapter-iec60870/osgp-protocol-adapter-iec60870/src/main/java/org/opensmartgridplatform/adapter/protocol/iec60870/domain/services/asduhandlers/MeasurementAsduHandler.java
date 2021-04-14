/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.AbstractClientAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.AsduConverterService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.DeviceResponseServiceRegistry;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/** Abstract class providing an implementation for handling Measurement ASDUs. */
public abstract class MeasurementAsduHandler extends AbstractClientAsduHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementAsduHandler.class);

  @Autowired private AsduConverterService converter;

  @Autowired private DeviceResponseServiceRegistry deviceResponseServiceRegistry;

  protected MeasurementAsduHandler(final ASduType asduType) {
    super(asduType);
  }

  @Override
  public void handleAsdu(final ASdu asdu, final ResponseMetadata responseMetadata) {
    LOGGER.debug("Received measurement of type {}.", asdu.getTypeIdentification());

    final MeasurementReportDto measurementReportDto = this.converter.convert(asdu);
    this.deviceResponseServiceRegistry
        .forDeviceType(responseMetadata.getDeviceType())
        .process(measurementReportDto, responseMetadata);
  }
}
