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
import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.AsduConverterService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.Iec60870ClientAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LoggingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.MeasurementReportingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseInfo;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class providing an implementation for handling Measurement ASDUs.
 *
 */
public abstract class MeasurementAsduHandler extends Iec60870ClientAsduHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementAsduHandler.class);

    @Autowired
    protected AsduConverterService converter;

    @Autowired
    protected MeasurementReportingService reportingService;

    @Autowired
    protected LoggingService loggingService;

    @Autowired
    protected CorrelationIdProviderService correlationIdProviderService;

    public MeasurementAsduHandler(final TypeId typeId) {
        super(typeId);
    }

    @Override
    public void handleAsdu(final ASdu asdu, final ResponseInfo responseInfo) throws IOException {
        LOGGER.info("Received measurement of type {}.", asdu.getTypeIdentification());
        final ResponseInfo newResponseInfo = this.createResponseInfoWithNewCorrelationId(responseInfo);
        final MeasurementReportDto measurementReportDto = this.converter.convert(asdu);
        this.reportingService.send(measurementReportDto, newResponseInfo);
        this.loggingService.log(this.createLogItem(asdu, newResponseInfo));
    }

    private LogItem createLogItem(final ASdu asdu, final ResponseInfo responseInfo) {
        return new LogItem(responseInfo.getDeviceIdentification(), responseInfo.getOrganisationIdentification(), true,
                true, asdu.toString(), 0);
    }

    private ResponseInfo createResponseInfoWithNewCorrelationId(final ResponseInfo responseInfo) {
        final String correlationUid = this.getCorrelationId(responseInfo);
        return new ResponseInfo.Builder(responseInfo).withCorrelationUid(correlationUid).build();
    }

    private String getCorrelationId(final ResponseInfo responseInfo) {

        return this.correlationIdProviderService.getCorrelationId(responseInfo.getOrganisationIdentification(),
                responseInfo.getDeviceIdentification());
    }
}
