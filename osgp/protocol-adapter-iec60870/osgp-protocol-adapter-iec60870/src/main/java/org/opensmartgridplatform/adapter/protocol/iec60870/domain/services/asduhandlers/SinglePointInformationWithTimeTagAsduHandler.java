/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.AbstractClientAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.AsduConverterService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.DeviceResponseService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.DeviceResponseServiceRegistry;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Class providing an implementation for handling single point information with time tag ASDUs. */
@Component
public class SinglePointInformationWithTimeTagAsduHandler extends AbstractClientAsduHandler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(SinglePointInformationWithTimeTagAsduHandler.class);

  @Autowired private AsduConverterService converter;

  @Autowired private DeviceResponseServiceRegistry deviceResponseServiceRegistry;

  public SinglePointInformationWithTimeTagAsduHandler() {
    super(ASduType.M_SP_TB_1);
  }

  @Override
  public void handleAsdu(final ASdu asdu, final ResponseMetadata responseMetadata) {
    LOGGER.debug(
        "Received asdu {} for device {}.", asdu, responseMetadata.getDeviceIdentification());

    final MeasurementReportDto measurementReportDto = this.converter.convert(asdu);

    final DeviceResponseService deviceResponseService =
        this.deviceResponseServiceRegistry.forDeviceType(responseMetadata.getDeviceType());
    if (CauseOfTransmission.SPONTANEOUS == asdu.getCauseOfTransmission()) {
      deviceResponseService.processEvent(measurementReportDto, responseMetadata);
    } else {
      deviceResponseService.process(measurementReportDto, responseMetadata);
    }
  }
}
