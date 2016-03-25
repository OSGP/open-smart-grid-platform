/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.application.jasper.sessionproviders.exceptions.SessionProviderException;
import org.osgp.adapter.protocol.dlms.application.services.ManagementService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionValueObjectResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.BundleResponseMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.EventDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.EventMessageDataContainerDto;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

/**
 * Class for processing find events request messages
 */
@Component("dlmsBundleMessageProcessor")
public class BundleMessageProcessor extends DeviceRequestMessageProcessor {

    @Autowired
    private ManagementService managementService;

    public BundleMessageProcessor() {
        super(DeviceRequestMessageType.BUNDLE);
    }

    @Override
    protected Serializable handleMessage(final ClientConnection conn, final DlmsDevice device,
            final Serializable requestObject) throws OsgpException, ProtocolAdapterException, SessionProviderException {

        // TODO: handle requestObject --> dispatch to all command executors and
        // put the result in the BundleResponseMessageDataContainerDto

        final EventDto eventDto = new EventDto(new DateTime(), 99, 4);

        final EventMessageDataContainerDto eventMessageDataContainerDto = new EventMessageDataContainerDto(
                Arrays.asList(eventDto));

        final List<ActionValueObjectResponseDto> actionValueObjectResponseDtoList = new ArrayList<ActionValueObjectResponseDto>();
        actionValueObjectResponseDtoList.add(eventMessageDataContainerDto);

        final BundleResponseMessageDataContainerDto bundleResponseMessageDataContainerDto = new BundleResponseMessageDataContainerDto(
                actionValueObjectResponseDtoList);

        return bundleResponseMessageDataContainerDto;
    }
}
