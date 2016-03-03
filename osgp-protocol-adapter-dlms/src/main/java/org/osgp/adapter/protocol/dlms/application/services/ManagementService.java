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

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.RetrieveEventsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.Event;
import com.alliander.osgp.dto.valueobjects.smartmetering.EventMessageDataContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQuery;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryMessageDataContainer;

@Service(value = "dlmsManagementService")
public class ManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementService.class);

    @Autowired
    private RetrieveEventsCommandExecutor retrieveEventsCommandExecutor;

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private DlmsConnectionFactory dlmsConnectionFactory;

    // === FIND EVENTS ===

    public EventMessageDataContainer findEvents(final ClientConnection conn, final DlmsDevice device,
            final FindEventsQueryMessageDataContainer findEventsQueryMessageDataContainer)
            throws ProtocolAdapterException {

        final List<Event> events = new ArrayList<>();

        LOGGER.info("findEvents setting up connection with meter {}", device.getDeviceIdentification());

        for (final FindEventsQuery findEventsQuery : findEventsQueryMessageDataContainer.getFindEventsQueryList()) {
            LOGGER.info("findEventsQuery.eventLogCategory: {}, findEventsQuery.from: {}, findEventsQuery.until: {}",
                    findEventsQuery.getEventLogCategory().toString(), findEventsQuery.getFrom(),
                    findEventsQuery.getUntil());

            events.addAll(this.retrieveEventsCommandExecutor.execute(conn, device, findEventsQuery));
        }

        return new EventMessageDataContainer(events);
    }

}
