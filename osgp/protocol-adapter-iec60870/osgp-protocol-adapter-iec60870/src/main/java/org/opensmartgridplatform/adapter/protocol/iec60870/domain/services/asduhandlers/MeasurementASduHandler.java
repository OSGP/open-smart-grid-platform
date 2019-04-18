/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers;

import java.io.IOException;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.AsduToMeasurementReportMapper;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.MeasurementReportMessageSender;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.iec60870.Iec60870ASduHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class MeasurementASduHandler extends Iec60870ASduHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementASduHandler.class);

    @Autowired
    protected AsduToMeasurementReportMapper mapper;

    @Autowired
    protected MeasurementReportMessageSender sender;

    public MeasurementASduHandler(final TypeId typeId) {
        super(typeId);
    }

    @Override
    public void handleASdu(final Connection connection, final ASdu asdu) throws IOException {
        LOGGER.info("Received measurement of type {}.", asdu.getTypeIdentification());
        final MeasurementReportDto measurementReportDto = this.mapper.convert(asdu);
        this.sender.send(measurementReportDto);
    }
}
