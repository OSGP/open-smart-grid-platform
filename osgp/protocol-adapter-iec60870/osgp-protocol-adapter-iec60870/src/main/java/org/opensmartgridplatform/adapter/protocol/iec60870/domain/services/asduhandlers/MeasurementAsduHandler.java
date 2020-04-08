/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.exceptions.AsduHandlerException;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.LogItemFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.ResponseMetadataFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.AsduConverterService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientAsduHandlerImpl;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.DeviceResponseServiceMap;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LoggingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class providing an implementation for handling Measurement ASDUs.
 *
 */
public abstract class MeasurementAsduHandler extends ClientAsduHandlerImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementAsduHandler.class);

    @Autowired
    private AsduConverterService converter;

    @Autowired
    private DeviceResponseServiceMap deviceResponseServiceMap;

    @Autowired
    private LoggingService loggingService;

    @Autowired
    protected ResponseMetadataFactory responseMetadataFactory;

    @Autowired
    private LogItemFactory logItemFactory;

    public MeasurementAsduHandler(final ASduType asduType) {
        super(asduType);
    }

    @Override
    public void handleAsdu(final ASdu asdu, final ResponseMetadata responseMetadata) throws AsduHandlerException {
        LOGGER.info("Received measurement of type {}.", asdu.getTypeIdentification());
        final ResponseMetadata newResponseMetadata = this.responseMetadataFactory
                .createWithNewCorrelationUid(responseMetadata);

        final MeasurementReportDto measurementReportDto = this.converter.convert(asdu);
        this.deviceResponseServiceMap.forDeviceType(responseMetadata.getDeviceType())
                .process(measurementReportDto, newResponseMetadata);

        final LogItem logItem = this.logItemFactory.create(asdu, newResponseMetadata, true);
        this.loggingService.log(logItem);
    }
}
