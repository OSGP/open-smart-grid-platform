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
import com.alliander.osgp.dto.valueobjects.smartmetering.GetAssociationLnObjectsRequestDto;

@Component
public class GetAssociationLnObjectsBundleCommandExecutorImpl extends
BundleCommandExecutor<GetAssociationLnObjectsRequestDto, ActionResponseDto> implements
        GetAssociationLnObjectsBundleCommandExecutor {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(GetAssociationLnObjectsBundleCommandExecutorImpl.class);

    @Autowired
    private GetAssociationLnObjectsCommandExecutor getAssociationLnObjectsCommandExecutor;

    public GetAssociationLnObjectsBundleCommandExecutorImpl() {
        super(GetAssociationLnObjectsRequestDto.class);
    }

    @Override
    public ActionResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final GetAssociationLnObjectsRequestDto getAssociationLnObjectsRequestDataDto) {

        try {
            return this.getAssociationLnObjectsCommandExecutor.execute(conn, device, null);
        } catch (final ProtocolAdapterException e) {

            LOGGER.error("Error retrieving association ln objects for device: " + device.getDeviceIdentification(), e);

            return new ActionResponseDto(e, "Error retrieving association ln objects for device: "
                    + device.getDeviceIdentification());
        }
    }
}
