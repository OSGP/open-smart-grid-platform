/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.application.mapping.ConfigurationMapper;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionValueObjectDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeResponseDto;

@Component()
public class GetAdministrativeStatusBundleCommandExecutor implements
        CommandExecutor<ActionValueObjectDto, AdministrativeStatusTypeResponseDto> {

    @Autowired
    GetAdministrativeStatusCommandExecutor getAdministrativeStatusCommandExecutor;

    @Autowired
    private ConfigurationMapper configurationMapper;

    @Override
    public AdministrativeStatusTypeResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final ActionValueObjectDto useless) throws ProtocolAdapterException {

        return new AdministrativeStatusTypeResponseDto(this.getAdministrativeStatusCommandExecutor.execute(conn,
                device, null));
    }
}
