/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.lightmeasurement.asduhandlers;

import java.util.List;
import java.util.Optional;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.ie.IeSinglePointWithQuality;
import org.openmuc.j60870.ie.InformationObject;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.LogItemFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.ResponseMetadataFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.lightmeasurement.LightMeasurementClientAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.lightmeasurement.LightMeasurementService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LoggingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.LogItem;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.dto.valueobjects.LightSensorStatusDto;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ASDU Handler for ASDUs with type identification M_SP_NA_1:.
 * <ul>
 * <li>Single-point information with quality</li>
 * </ul>
 */
@Component
public class SinglePointWithQualityAsduHandler extends LightMeasurementClientAsduHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SinglePointWithQualityAsduHandler.class);

    @Autowired
    private LightMeasurementService lightMeasurementService;

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private ResponseMetadataFactory responseMetadataFactory;

    @Autowired
    private LogItemFactory logItemFactory;

    @Autowired
    private Iec60870DeviceRepository iec60870DeviceRepository;

    public SinglePointWithQualityAsduHandler() {
        super(ASduType.M_SP_NA_1);
    }

    @Override
    public void handleAsdu(final ASdu asdu, final ResponseMetadata responseMetadata) {
        LOGGER.info("Received single-point information with quality command {}.", asdu);
        final ResponseMetadata newResponseMetadata = this.responseMetadataFactory
                .createWithNewCorrelationUid(responseMetadata);

        final List<Iec60870Device> lightMeasurementDevices = this.iec60870DeviceRepository
                .findByGatewayDeviceIdentification(responseMetadata.getDeviceIdentification());

        this.sendLightSensorStatusResponses(asdu, lightMeasurementDevices, newResponseMetadata);

        final LogItem logItem = this.logItemFactory.create(asdu, newResponseMetadata, true);
        this.loggingService.log(logItem);
    }

    private void sendLightSensorStatusResponses(final ASdu asdu, final List<Iec60870Device> lightMeasurementDevices,
            final ResponseMetadata responseMetadata) {

        for (final InformationObject io : asdu.getInformationObjects()) {
            final Optional<Iec60870Device> device = lightMeasurementDevices.stream()
                    .filter(d -> d.getInformationObjectAddress() == io.getInformationObjectAddress())
                    .findFirst();
            device.ifPresent(d -> this.sendLightSensorStatusResponse(io, d, responseMetadata));
        }
    }

    private void sendLightSensorStatusResponse(final InformationObject io, final Iec60870Device device,
            final ResponseMetadata responseMetadata) {
        final ResponseMetadata rm = new ResponseMetadata.Builder()
                .withCorrelationUid(responseMetadata.getCorrelationUid())
                .withDeviceIdentification(device.getDeviceIdentification())
                .withDomainInfo(device.getDeviceType().domainType().domainInfo())
                .withMessageType(MessageType.GET_LIGHT_SENSOR_STATUS.name())
                .withOrganisationIdentification(responseMetadata.getOrganisationIdentification())
                .build();

        final IeSinglePointWithQuality siq = (IeSinglePointWithQuality) io.getInformationElements()[0][0];
        final LightSensorStatusDto dto = new LightSensorStatusDto(siq.isOn());

        this.lightMeasurementService.send(dto, rm);
    }
}
