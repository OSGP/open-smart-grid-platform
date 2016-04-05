/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.List;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.EventDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.EventMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryDto;

@Component()
public class RetrieveEventsBundleCommandExecutor implements
        CommandExecutor<FindEventsQueryDto, EventMessageDataContainerDto> {

    @Autowired
    RetrieveEventsCommandExecutor retrieveEventsCommandExecutor;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public EventMessageDataContainerDto execute(final ClientConnection conn, final DlmsDevice device,
            final FindEventsQueryDto findEventsQuery) throws ProtocolAdapterException {

        List<EventDto> eventDtoList;
        eventDtoList = this.retrieveEventsCommandExecutor.execute(conn, device, findEventsQuery);

        return new EventMessageDataContainerDto(eventDtoList);
    }
}
