/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import org.joda.time.DateTime;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.SynchronizeTimeRequest;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "dlmsAdhocService")
public class AdhocService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdhocService.class);

    /**
     * Constructor
     */
    public AdhocService() {
        // Parameterless constructor required for transactions...
    }

    // === REQUEST Synchronize Time DATA ===

    public void requestSynchronizeTime(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final SynchronizeTimeRequest synchronizeTimeRequest,
            final DeviceResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType) {

        LOGGER.info("requestSynchronizeTime called for device: {} for organisation: {}", deviceIdentification,
                organisationIdentification);

        try {
            // Synchronize Time has for now no return data, like a deviation
            // time.

            final DateTime newSmartMeterTime = new DateTime();

            LOGGER.info(new String("************************************************************").substring(0, 55));
            LOGGER.info(new String("*********** Synchronize Time SmartMeter ********************").substring(0, 55));
            LOGGER.info(new String("************************************************************").substring(0, 55));
            LOGGER.info(new String("************************************************************").substring(0, 55));
            LOGGER.info(new String("*********   Year:       {}   *******************************").substring(0, 53),
                    newSmartMeterTime.getYear());
            LOGGER.info(new String("*********   Month:      {}   *******************************").substring(0, 55),
                    newSmartMeterTime.getMonthOfYear());
            LOGGER.info(new String("*********   Day:        {}   *******************************").substring(0, 56),
                    newSmartMeterTime.getDayOfMonth());
            LOGGER.info(new String("*********   Hour:       {}   *******************************").substring(0, 55),
                    newSmartMeterTime.getHourOfDay());
            LOGGER.info(new String("*********   Minutes:    {}   *******************************").substring(0, 55),
                    newSmartMeterTime.getMinuteOfHour());
            LOGGER.info(new String("*********   Seconds:    {}   *******************************").substring(0, 55),
                    newSmartMeterTime.getSecondOfMinute());
            LOGGER.info(new String("************************************************************").substring(0, 55));
            LOGGER.info(new String("************************************************************").substring(0, 55));
            LOGGER.info(new String("************************************************************").substring(0, 55));

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during synchronizeTime", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception during synchronizeTime", e);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        }
    }

    private void sendResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException,
            final DeviceResponseMessageSender responseMessageSender) {

        // Creating a ProtocolResponseMessage without a Serializable object
        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType,
                correlationUid, organisationIdentification, deviceIdentification, result, osgpException, null);

        responseMessageSender.send(responseMessage);
    }
}
