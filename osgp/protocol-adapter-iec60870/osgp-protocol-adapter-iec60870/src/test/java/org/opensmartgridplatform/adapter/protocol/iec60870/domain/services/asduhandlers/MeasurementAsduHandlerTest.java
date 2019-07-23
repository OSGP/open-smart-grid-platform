/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.TypeId;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.Iec60870Mapper;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.AsduFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.LogItemFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.ResponseMetadataFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.AsduConverterService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LoggingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.MeasurementReportingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;

import ma.glasnost.orika.MapperFacade;

/**
 * org.mockito.exceptions.base.MockitoException: 
 *Mockito cannot mock this class: class org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.ResponseMetadataFactory.
 *Mockito can only mock non-private & non-final classes.
 */
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class MeasurementAsduHandlerTest {

    private static final String DEVICE_IDENTIFICATION = "TEST-DEVICE-1";
    private static final String ORGANISATION_IDENTIFICATION = "TEST-ORG-1";
    private static final String CORRELATION_UID = "TEST-CORR-1";

    @InjectMocks
    private ShortFloatWithTime56MeasurementAsduHandler asduHandler;

    @Mock
    private ResponseMetadataFactory responseMetadataFactory;

    @Mock
    private AsduConverterService converter;

    @Mock
    private LogItemFactory logItemFactory;

    @Mock
    private MeasurementReportingService reportingService;

    @Mock
    private LoggingService loggingService;

    private MapperFacade mapper = new Iec60870Mapper();

    @Test
    public void shouldSendMeasurementReportAndLogItemWhenHandlingAsdu() {
        // Arrange
        final ASdu asdu = AsduFactory.ofType(TypeId.M_ME_TF_1);
        final MeasurementReportDto measurementReportDto = this.mapper.map(asdu, MeasurementReportDto.class);
        final ResponseMetadata responseMetadata = new ResponseMetadata.Builder().withCorrelationUid(CORRELATION_UID)
                .withDeviceIdentification(DEVICE_IDENTIFICATION)
                .withOrganisationIdentification(ORGANISATION_IDENTIFICATION).build();
        final LogItem logItem = new LogItem(DEVICE_IDENTIFICATION, ORGANISATION_IDENTIFICATION, true, asdu.toString());

        when(this.responseMetadataFactory.createWithNewCorrelationUid(responseMetadata)).thenReturn(responseMetadata);
        when(this.converter.convert(asdu)).thenReturn(measurementReportDto);
        when(this.logItemFactory.create(asdu, responseMetadata, true)).thenReturn(logItem);
        // Act
        this.asduHandler.handleAsdu(asdu, responseMetadata);

        // Assert
        verify(this.reportingService).send(measurementReportDto, responseMetadata);
        verify(this.loggingService).log(logItem);
    }
}
