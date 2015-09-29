/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.Event;
import com.alliander.osgp.dto.valueobjects.smartmetering.EventMessageDataContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryMessageDataContainer;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "dlmsManagementService")
public class ManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementService.class);

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    public ManagementService() {
        // Parameterless constructor required for transactions...
    }

    // === FIND EVENTS ===

    public void findEvents(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final DeviceResponseMessageSender responseMessageSender, final String domain,
            final String domainVersion, final String messageType,
            final FindEventsQueryMessageDataContainer findEventsQueryMessageDataContainer) {

        LOGGER.info("findEvents called for device: {} for organisation: {}", deviceIdentification,
                organisationIdentification);

        try {
            LOGGER.info("FindEventsQueryMessageDataContainer number of FindEventsQuery: {}",
                    findEventsQueryMessageDataContainer.getFindEventsQueryList().size());

            // TODO: talk to the smart-meter and fetch the events.
            // For now, just create some dummy data to return.

            final List<Event> events = new ArrayList<>();
            events.add(new Event(new DateTime(12344566L), 11));
            events.add(new Event(new DateTime(66554432L), 12));
            final EventMessageDataContainer eventMessageDataContainer = new EventMessageDataContainer(events);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.OK, null, responseMessageSender,
                    eventMessageDataContainer);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during findEvents", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.NOT_OK, ex, responseMessageSender, null);
        }
    }

    private void sendResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException,
            final DeviceResponseMessageSender responseMessageSender, final Serializable dataObject) {

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType,
                correlationUid, organisationIdentification, deviceIdentification, result, osgpException, dataObject);

        responseMessageSender.send(responseMessage);
    }
}
