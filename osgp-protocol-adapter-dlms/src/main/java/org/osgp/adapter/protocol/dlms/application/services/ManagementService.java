/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.util.ArrayList;
import java.util.List;

import org.openmuc.jdlms.LnClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.RetrieveEventsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.Event;
import com.alliander.osgp.dto.valueobjects.smartmetering.EventMessageDataContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQuery;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryMessageDataContainer;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "dlmsManagementService")
public class ManagementService extends DlmsApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementService.class);

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private RetrieveEventsCommandExecutor retrieveEventsCommandExecutor;

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private DlmsConnectionFactory dlmsConnectionFactory;

    // === FIND EVENTS ===

    public void findEvents(final DlmsDeviceMessageMetadata messageMetadata,
            final DeviceResponseMessageSender responseMessageSender,
            final FindEventsQueryMessageDataContainer findEventsQueryMessageDataContainer) {

        this.logStart(LOGGER, messageMetadata, "findEvents");

        final List<Event> events = new ArrayList<>();

        LnClientConnection conn = null;
        try {

            final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);

            LOGGER.info("findEvents setting up connection with meter {}", device.getDeviceIdentification());

            conn = this.dlmsConnectionFactory.getConnection(device);

            for (final FindEventsQuery findEventsQuery : findEventsQueryMessageDataContainer.getFindEventsQueryList()) {
                LOGGER.info(
                        "findEventsQuery.eventLogCategory: {}, findEventsQuery.from: {}, findEventsQuery.until: {}",
                        findEventsQuery.getEventLogCategory().toString(), findEventsQuery.getFrom(),
                        findEventsQuery.getUntil());

                events.addAll(this.retrieveEventsCommandExecutor.execute(conn, findEventsQuery));
            }

            final EventMessageDataContainer eventMessageDataContainer = new EventMessageDataContainer(events);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender,
                    eventMessageDataContainer);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during findEvents", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender,
                    findEventsQueryMessageDataContainer);
        }
    }

}
