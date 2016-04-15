/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.EventMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryDto;

@Component()
public class RetrieveEventsBundleCommandExecutor implements CommandExecutor<FindEventsQueryDto, ActionResponseDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveEventsBundleCommandExecutor.class);

    @Autowired
    RetrieveEventsCommandExecutor retrieveEventsCommandExecutor;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public ActionResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final FindEventsQueryDto findEventsQuery) {

        try {
            return new EventMessageDataContainerDto(this.retrieveEventsCommandExecutor.execute(conn, device,
                    findEventsQuery));
        } catch (final ProtocolAdapterException e) {
            LOGGER.error("Error while retrieving " + findEventsQuery.getEventLogCategory() + "events from device: "
                    + device.getDeviceIdentification(), e);
            return new ActionResponseDto(e, "Error while retrieving " + findEventsQuery.getEventLogCategory()
                    + "events from device: " + device.getDeviceIdentification());
        }
    }
}
