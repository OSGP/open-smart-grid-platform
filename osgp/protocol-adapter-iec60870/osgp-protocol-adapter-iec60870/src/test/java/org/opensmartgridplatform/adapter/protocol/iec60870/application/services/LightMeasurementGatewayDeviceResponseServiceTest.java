/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.Iec60870DeviceFactory.KEY_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.Iec60870DeviceFactory.KEY_DEVICE_TYPE;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.Iec60870DeviceFactory.KEY_GATEWAY_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.Iec60870DeviceFactory.KEY_INFORMATION_OBJECT_ADDRESS;
import static org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories.Iec60870DeviceFactory.fromSettings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.ResponseMetadataFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LightMeasurementService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.dto.da.measurements.MeasurementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementGroupDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportHeaderDto;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;
import org.opensmartgridplatform.dto.valueobjects.LightSensorStatusDto;

@ExtendWith(MockitoExtension.class)
public class LightMeasurementGatewayDeviceResponseServiceTest {

    private static final String GATEWAY_DEVICE_IDENTIFICATION = "TEST-GATEWAY-1";
    private static final String LMD_1_DEVICE_IDENTIFICATION = "TEST-LMD-1";
    private static final String LMD_1_IOA = "1";
    private static final byte LMD_1_ON = 1;
    private static final String LMD_2_DEVICE_IDENTIFICATION = "TEST-LMD-2";
    private static final String LMD_2_IOA = "2";
    private static final byte LMD_2_ON = 0;
    private static final String ORGANISATION_IDENTIFICATION = "TEST-ORG-1";
    private static final String CORRELATION_UID = "TEST-CORR-1";

    @InjectMocks
    private LightMeasurementGatewayDeviceResponseService lightMeasurementGatewayDeviceResponseService;

    @Mock
    private Iec60870DeviceRepository iec60870DeviceRepository;

    @Mock
    private ResponseMetadataFactory responseMetadataFactory;

    @Mock
    private LightMeasurementService lightMeasurementService;

    /**
     * Test method for
     * {@link org.opensmartgridplatform.adapter.protocol.iec60870.application.services.LightMeasurementGatewayDeviceResponseService#process(org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto, org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata)}.
     */
    @Test
    void testProcessShouldSendLightSensorStatusReports() {
        // Arrange
        final Iec60870Device gatewayDevice = fromSettings(this.getGatewayDeviceSettings());
        final Iec60870Device lightMeasurementDevice1 = fromSettings(this.getLightMeasurementDevice1Settings());
        final Iec60870Device lightMeasurementDevice2 = fromSettings(this.getLightMeasurementDevice2Settings());
        when(this.iec60870DeviceRepository.findByGatewayDeviceIdentification(gatewayDevice.getDeviceIdentification()))
                .thenReturn(Arrays.asList(lightMeasurementDevice1, lightMeasurementDevice2));

        final MeasurementReportDto measurementReportDto = this.getMeasurementReportDto();

        final ResponseMetadata responseMetadata = this.getResponseMetadata(GATEWAY_DEVICE_IDENTIFICATION);
        when(this.responseMetadataFactory.createWithNewCorrelationUid(responseMetadata)).thenReturn(responseMetadata);

        // Act
        this.lightMeasurementGatewayDeviceResponseService.process(measurementReportDto, responseMetadata);

        // Assert
        verify(this.lightMeasurementService, new Times(2)).send(any(LightSensorStatusDto.class),
                any(ResponseMetadata.class));
    }

    private ResponseMetadata getResponseMetadata(final String deviceIdentification) {
        return new ResponseMetadata.Builder().withCorrelationUid(CORRELATION_UID)
                .withDeviceIdentification(deviceIdentification)
                .withOrganisationIdentification(ORGANISATION_IDENTIFICATION)
                .build();
    }

    private MeasurementReportDto getMeasurementReportDto() {
        final MeasurementReportHeaderDto mrh = new MeasurementReportHeaderDto("M_SP_NA_1", "INTERROGATED_BY_STATION", 0,
                0);
        final MeasurementGroupDto mg1 = this.getMeasurementGroup(LMD_1_IOA, LMD_1_ON);
        final MeasurementGroupDto mg2 = this.getMeasurementGroup(LMD_2_IOA, LMD_2_ON);
        return new MeasurementReportDto(mrh, Arrays.asList(mg1, mg2));
    }

    private MeasurementGroupDto getMeasurementGroup(final String identification, final byte value) {
        final MeasurementElementDto me = new BitmaskMeasurementElementDto(value);
        final MeasurementDto m = new MeasurementDto(Arrays.asList(me));
        return new MeasurementGroupDto(identification, Arrays.asList(m));
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
        settings.put(KEY_INFORMATION_OBJECT_ADDRESS, LMD_1_IOA);
        return settings;
    }

    private Map<String, String> getLightMeasurementDevice2Settings() {
        final Map<String, String> settings = new HashMap<String, String>();
        settings.put(KEY_DEVICE_IDENTIFICATION, LMD_2_DEVICE_IDENTIFICATION);
        settings.put(KEY_DEVICE_TYPE, DeviceType.LIGHT_MEASUREMENT_DEVICE.name());
        settings.put(KEY_GATEWAY_DEVICE_IDENTIFICATION, GATEWAY_DEVICE_IDENTIFICATION);
        settings.put(KEY_INFORMATION_OBJECT_ADDRESS, LMD_2_IOA);
        return settings;
    }

}
