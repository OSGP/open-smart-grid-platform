/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import java.util.Optional;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.exceptions.AsduHandlerException;
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
public class LightMeasurementDeviceResponseService extends AbstractDeviceResponseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LightMeasurementDeviceResponseService.class);

    private static final DeviceType DEVICE_TYPE = DeviceType.LIGHT_MEASUREMENT_DEVICE;

    @Autowired
    private Iec60870DeviceRepository iec60870DeviceRepository;

    @Autowired
    private ResponseMetadataFactory responseMetadataFactory;

    @Autowired
    private LightMeasurementService lightMeasurementService;

    public LightMeasurementDeviceResponseService() {
        super(DEVICE_TYPE);
    }

    @Override
    public void process(final MeasurementReportDto measurementReportDto, final ResponseMetadata responseMetadata)
            throws AsduHandlerException {
        LOGGER.info("Received measurement report {} for light measurement gateway {}.", measurementReportDto,
                responseMetadata.getDeviceIdentification());

        final ResponseMetadata newResponseMetadata = this.responseMetadataFactory
                .createWithNewCorrelationUid(responseMetadata);

        final Iec60870Device device = this.iec60870DeviceRepository
                .findByDeviceIdentification(responseMetadata.getDeviceIdentification())
                .orElseThrow(AsduHandlerException.withMessage("Device not found."));

        this.sendLightSensorStatusResponse(measurementReportDto, device, newResponseMetadata);
    }

    private void sendLightSensorStatusResponse(final MeasurementReportDto measurementReportDto,
            final Iec60870Device device, final ResponseMetadata responseMetadata) {

        final Optional<MeasurementGroupDto> measurementGroupDto = measurementReportDto.getMeasurementGroups()
                .stream()
                .filter(mg -> mg.getIdentification().equals(device.getInformationObjectAddress().toString()))
                .findFirst();
        measurementGroupDto.ifPresent(mg -> this.sendLightSensorStatusResponse(mg, device, responseMetadata));
    }

    private void sendLightSensorStatusResponse(final MeasurementGroupDto measurementGroupDto,
            final Iec60870Device device, final ResponseMetadata responseMetadata) {
        final ResponseMetadata rm = new ResponseMetadata.Builder()
                .withCorrelationUid(responseMetadata.getCorrelationUid())
                .withDeviceIdentification(device.getDeviceIdentification())
                .withDomainInfo(device.getDeviceType().domainType().domainInfo())
                .withMessageType(MessageType.GET_LIGHT_SENSOR_STATUS.name())
                .withOrganisationIdentification(responseMetadata.getOrganisationIdentification())
                .build();

        final boolean on = ((BitmaskMeasurementElementDto) measurementGroupDto.getMeasurements()
                .get(0)
                .getMeasurementElements()
                .get(0)).getValue() == 1;
        final LightSensorStatusDto dto = new LightSensorStatusDto(on);

        this.lightMeasurementService.send(dto, rm);
    }

}
