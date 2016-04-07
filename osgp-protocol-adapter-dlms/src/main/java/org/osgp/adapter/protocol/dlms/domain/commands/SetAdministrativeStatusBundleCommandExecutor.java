/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionValueObjectResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;

@Component()
public class SetAdministrativeStatusBundleCommandExecutor implements
CommandExecutor<AdministrativeStatusTypeDataDto, ActionValueObjectResponseDto> {

    @Autowired
    private SetAdministrativeStatusCommandExecutor setAdministrativeStatusCommandExecutor;

    @Override
    public ActionValueObjectResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final AdministrativeStatusTypeDataDto administrativeStatusType) throws ProtocolAdapterException {

        final AdministrativeStatusTypeDto adminStatusType = administrativeStatusType.getAdministrativeStatusType();

        try {

            final AccessResultCode resultCode = this.setAdministrativeStatusCommandExecutor.execute(conn, device,
                    adminStatusType);
            if (AccessResultCode.SUCCESS.equals(resultCode)) {
                return new ActionValueObjectResponseDto("Set administrative status to " + adminStatusType
                        + " was successful");
            } else {
                return new ActionValueObjectResponseDto("Set administrative status to " + adminStatusType
                        + " was not successful. Result code: " + resultCode);
            }
        } catch (final ProtocolAdapterException e) {
            return new ActionValueObjectResponseDto(e, "Set administrative status to " + adminStatusType
                    + " was not successful");
        }

    }
}
