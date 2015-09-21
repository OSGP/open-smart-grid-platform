/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.util.Random;

import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequest;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestData;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "dlmsDeviceMonitoringService")
public class MonitoringService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringService.class);

    private static final Random generator = new Random();

    /**
     * Constructor
     */
    public MonitoringService() {
        // Parameterless constructor required for transactions...
    }

    // === REQUEST PERIODIC METER DATA ===

    public void requestPeriodicMeterReads(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final PeriodicMeterReadsRequest periodicMeterReadsRequest,
            final DeviceResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType) {

        LOGGER.info("requestPeriodicMeterReads called for device: {} for organisation: {}", deviceIdentification,
                organisationIdentification);

        try {
            // creating duMy periodicMeterReads

            final PeriodicMeterReads periodicMeterReads = new PeriodicMeterReads();
            periodicMeterReads.setDeviceIdentification(deviceIdentification);

            MeterReads MeterReads;
            for (final PeriodicMeterReadsRequestData p : periodicMeterReadsRequest.getPeriodicMeterReadsRequestData()) {
                // DuMy MeterReads with random values
                MeterReads = new MeterReads();
                MeterReads.setLogTime(p.getDate());
                MeterReads.setActiveEnergyImportTariffOne(Math.abs(generator.nextLong()));
                MeterReads.setActiveEnergyImportTariffTwo(Math.abs(generator.nextLong()));
                MeterReads.setActiveEnergyExportTariffOne(Math.abs(generator.nextLong()));
                MeterReads.setActiveEnergyExportTariffTwo(Math.abs(generator.nextLong()));

                MeterReads.setPeriodicMeterReads(periodicMeterReads);
                periodicMeterReads.addMeterReads(MeterReads);
            }

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.OK, null, responseMessageSender, periodicMeterReads);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during requestPeriodicMeterReads", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.NOT_OK, ex, responseMessageSender, null);
        }
    }

    private void sendResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException,
            final DeviceResponseMessageSender responseMessageSender, final PeriodicMeterReads periodicMeterReads) {

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType,
                correlationUid, organisationIdentification, deviceIdentification, result, osgpException,
                periodicMeterReads);

        responseMessageSender.send(responseMessage);
    }
}
