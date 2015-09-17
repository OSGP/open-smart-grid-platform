/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.util.Date;

import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.MeterData;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterData;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequest;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "dlmsDeviceMonitoringService")
public class MonitoringService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringService.class);

    /**
     * Constructor
     */
    public MonitoringService() {
        // Parameterless constructor required for transactions...
    }

    // === REQUEST PERIODIC METER DATA ===

    public void requestPeriodicMeterData(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final PeriodicMeterReadsRequest periodicMeterReadsRequest,
            final DeviceResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType) {

        LOGGER.info("requestPeriodicMeterData called for device: {} for organisation: {}", deviceIdentification,
                organisationIdentification);

        try {
            // creating dummy periodicMeterData

            final PeriodicMeterData periodicMeterData = new PeriodicMeterData();
            periodicMeterData.setDeviceIdentification(deviceIdentification);

            final MeterData meterData1 = new MeterData();
            meterData1.setCaptureTime(new Date());
            meterData1.setLogTime(new Date());
            meterData1.setMeterValue(123);
            meterData1.setPeriodicMeterData(periodicMeterData);

            final MeterData meterData2 = new MeterData();
            meterData2.setCaptureTime(new Date());
            meterData2.setLogTime(new Date());
            meterData2.setMeterValue(123);
            meterData2.setPeriodicMeterData(periodicMeterData);

            periodicMeterData.addMeterData(meterData1);
            periodicMeterData.addMeterData(meterData2);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.OK, null, responseMessageSender, periodicMeterData);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during requestPeriodicMeterData", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.NOT_OK, ex, responseMessageSender, null);
        }
    }

    private void sendResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException,
            final DeviceResponseMessageSender responseMessageSender, final PeriodicMeterData periodicMeterData) {

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType,
                correlationUid, organisationIdentification, deviceIdentification, result, osgpException,
                periodicMeterData);

        responseMessageSender.send(responseMessage);
    }
}
