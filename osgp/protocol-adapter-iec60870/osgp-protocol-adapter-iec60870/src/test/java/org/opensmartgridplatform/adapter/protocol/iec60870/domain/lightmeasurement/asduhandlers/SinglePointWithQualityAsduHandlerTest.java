/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.lightmeasurement.asduhandlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.Iec60870DeviceFactory.KEY_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.Iec60870DeviceFactory.KEY_DEVICE_TYPE;
import static org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.Iec60870DeviceFactory.KEY_GATEWAY_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.Iec60870DeviceFactory.KEY_INFORMATION_OBJECT_ADDRESS;
import static org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.Iec60870DeviceFactory.fromSettings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeSinglePointWithQuality;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.LogItemFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.ResponseMetadataFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.lightmeasurement.LightMeasurementService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LoggingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.adapter.protocol.iec60870.testutils.builders.AsduBuilder;
import org.opensmartgridplatform.dto.valueobjects.LightSensorStatusDto;

@ExtendWith(MockitoExtension.class)
public class SinglePointWithQualityAsduHandlerTest {

    private static final String GATEWAY_DEVICE_IDENTIFICATION = "TEST-GATEWAY-1";
    private static final String LMD_1_DEVICE_IDENTIFICATION = "TEST-LMD-1";
    private static final int LMD_1_IOA = 1;
    private static final boolean LMD_1_ON = true;
    private static final String LMD_2_DEVICE_IDENTIFICATION = "TEST-LMD-2";
    private static final int LMD_2_IOA = 2;
    private static final boolean LMD_2_ON = false;
    private static final String ORGANISATION_IDENTIFICATION = "TEST-ORG-1";
    private static final String CORRELATION_UID = "TEST-CORR-1";

    @InjectMocks
    private SinglePointWithQualityAsduHandler asduHandler;

    @Mock
    private Iec60870DeviceRepository iec60870DeviceRepository;

    @Mock
    private ResponseMetadataFactory responseMetadataFactory;

    @Mock
    private LogItemFactory logItemFactory;

    @Mock
    private LightMeasurementService lightMeasurementService;

    @Mock
    private LoggingService loggingService;

    /**
     * Test method for
     * {@link org.opensmartgridplatform.adapter.protocol.iec60870.domain.lightmeasurement.asduhandlers.SinglePointWithQualityAsduHandler#handleAsdu(org.openmuc.j60870.ASdu, org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata)}.
     */
    @Test
    public void testHandleAsduShouldSendMultipleGetLightSensorStatusResponsesAndLogItemWhenGatewayDevice() {
        // Arrange
        final Iec60870Device gatewayDevice = fromSettings(this.getGatewayDeviceSettings());
        final Iec60870Device lightMeasurementDevice1 = fromSettings(this.getLightMeasurementDevice1Settings());
        final Iec60870Device lightMeasurementDevice2 = fromSettings(this.getLightMeasurementDevice2Settings());
        when(this.iec60870DeviceRepository.findByGatewayDeviceIdentification(gatewayDevice.getDeviceIdentification()))
                .thenReturn(Arrays.asList(lightMeasurementDevice1, lightMeasurementDevice2));

        final ASdu asdu = this.createAsdu();

        final ResponseMetadata responseMetadata = this.getResponseMetadata(GATEWAY_DEVICE_IDENTIFICATION);
        when(this.responseMetadataFactory.createWithNewCorrelationUid(responseMetadata)).thenReturn(responseMetadata);

        final LogItem logItem = new LogItem(GATEWAY_DEVICE_IDENTIFICATION, ORGANISATION_IDENTIFICATION, true,
                asdu.toString());
        when(this.logItemFactory.create(asdu, responseMetadata, true)).thenReturn(logItem);

        // Act
        this.asduHandler.handleAsdu(asdu, responseMetadata);

        // Assert
        verify(this.lightMeasurementService, new Times(2)).send(any(LightSensorStatusDto.class),
                any(ResponseMetadata.class));
        verify(this.loggingService).log(logItem);
    }

    private ASdu createAsdu() {
        final InformationObject ioLmd1 = this.createInformationObject(LMD_1_IOA, LMD_1_ON);
        final InformationObject ioLmd2 = this.createInformationObject(LMD_2_IOA, LMD_2_ON);

        return AsduBuilder.ofType(ASduType.M_SP_NA_1)
                .withCauseOfTransmission(CauseOfTransmission.INTERROGATED_BY_STATION)
                .withInformationObjects(ioLmd1, ioLmd2)
                .build();
    }

    private InformationObject createInformationObject(final int ioa, final boolean on) {
        return new InformationObject(ioa, new IeSinglePointWithQuality(on, false, false, false, false));
    }

    private ResponseMetadata getResponseMetadata(final String deviceIdentification) {
        return new ResponseMetadata.Builder().withCorrelationUid(CORRELATION_UID)
                .withDeviceIdentification(deviceIdentification)
                .withOrganisationIdentification(ORGANISATION_IDENTIFICATION)
                .build();
    }

    private Map<String, String> getGatewayDeviceSettings() {
        final Map<String, String> settings = new HashMap<String, String>();
        settings.put(KEY_DEVICE_IDENTIFICATION, GATEWAY_DEVICE_IDENTIFICATION);
        settings.put(KEY_DEVICE_TYPE, DeviceType.LIGHT_MEASUREMENT_GATEWAY.name());
        return settings;
    }

    private Map<String, String> getLightMeasurementDevice1Settings() {
        final Map<String, String> settings = new HashMap<String, String>();
        settings.put(KEY_DEVICE_IDENTIFICATION, LMD_1_DEVICE_IDENTIFICATION);
        settings.put(KEY_DEVICE_TYPE, DeviceType.LIGHT_MEASUREMENT_DEVICE.name());
        settings.put(KEY_GATEWAY_DEVICE_IDENTIFICATION, GATEWAY_DEVICE_IDENTIFICATION);
        settings.put(KEY_INFORMATION_OBJECT_ADDRESS, Integer.toString(LMD_1_IOA));
        return settings;
    }

    private Map<String, String> getLightMeasurementDevice2Settings() {
        final Map<String, String> settings = new HashMap<String, String>();
        settings.put(KEY_DEVICE_IDENTIFICATION, LMD_2_DEVICE_IDENTIFICATION);
        settings.put(KEY_DEVICE_TYPE, DeviceType.LIGHT_MEASUREMENT_DEVICE.name());
        settings.put(KEY_GATEWAY_DEVICE_IDENTIFICATION, GATEWAY_DEVICE_IDENTIFICATION);
        settings.put(KEY_INFORMATION_OBJECT_ADDRESS, Integer.toString(LMD_2_IOA));
        return settings;
    }
}
