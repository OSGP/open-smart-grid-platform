/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import java.util.List;
import java.util.Optional;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.ResponseMetadataFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LightMeasurementService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.dto.da.measurements.MeasurementGroupDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;
import org.opensmartgridplatform.dto.valueobjects.LightSensorStatusDto;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LightMeasurementGatewayDeviceResponseService extends AbstractDeviceResponseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LightMeasurementGatewayDeviceResponseService.class);

    private static final String ERROR_PREFIX = "Error while processing measurement report for light measurement gateway: ";

    private static final DeviceType DEVICE_TYPE = DeviceType.LIGHT_MEASUREMENT_GATEWAY;

    @Autowired
    private Iec60870DeviceRepository iec60870DeviceRepository;

    @Autowired
    private ResponseMetadataFactory responseMetadataFactory;

    @Autowired
    private LightMeasurementService lightMeasurementService;

    public LightMeasurementGatewayDeviceResponseService() {
        super(DEVICE_TYPE);
    }

    @Override
    public void process(final MeasurementReportDto measurementReportDto, final ResponseMetadata responseMetadata) {
        LOGGER.info("Received measurement report {} for light measurement gateway {}.", measurementReportDto,
                responseMetadata.getDeviceIdentification());

        final List<Iec60870Device> lightMeasurementDevices = this.iec60870DeviceRepository
                .findByGatewayDeviceIdentification(responseMetadata.getDeviceIdentification());

        this.sendLightSensorStatusResponses(measurementReportDto, lightMeasurementDevices, responseMetadata);
    }

    private void sendLightSensorStatusResponses(final MeasurementReportDto measurementReportDto,
            final List<Iec60870Device> lightMeasurementDevices, final ResponseMetadata responseMetadata) {

        for (final MeasurementGroupDto mg : measurementReportDto.getMeasurementGroups()) {
            final Optional<Iec60870Device> device = lightMeasurementDevices.stream()
                    .filter(d -> d.getInformationObjectAddress().toString().equals(mg.getIdentification()))
                    .findFirst();

            if (device.isPresent()) {
                this.sendLightSensorStatusResponse(mg, device.get(), responseMetadata);
            } else {
                LOGGER.error("{} No device found for measurement group {}", ERROR_PREFIX, mg.getIdentification());
            }
        }
    }

    private void sendLightSensorStatusResponse(final MeasurementGroupDto measurementGroupDto,
            final Iec60870Device device, final ResponseMetadata responseMetadata) {
        final ResponseMetadata rm = new ResponseMetadata.Builder()
                .withCorrelationUid(responseMetadata.getCorrelationUid())
                .withDeviceIdentification(device.getDeviceIdentification())
                .withDomainInfo(responseMetadata.getDomainInfo())
                .withMessageType(MessageType.GET_LIGHT_SENSOR_STATUS.name())
                .withOrganisationIdentification(responseMetadata.getOrganisationIdentification())
                .build();

        final Optional<Boolean> on = measurementGroupDto.getMeasurements()
                .stream()
                .flatMap(m -> m.getMeasurementElements().stream())
                .filter(me -> me instanceof BitmaskMeasurementElementDto)
                .findFirst()
                .map(me -> ((BitmaskMeasurementElementDto) me).getValue() == 1);

        if (on.isPresent()) {
            final LightSensorStatusDto dto = new LightSensorStatusDto(on.get());
            this.lightMeasurementService.send(dto, rm);
        } else {
            LOGGER.error("{} Light sensor status information not found for device {}", ERROR_PREFIX,
                    device.getDeviceIdentification());
        }
    }

}
