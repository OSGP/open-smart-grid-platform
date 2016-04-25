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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;

@Component()
public class SetAdministrativeStatusBundleCommandExecutorImpl extends
        BundleCommandExecutor<AdministrativeStatusTypeDataDto, ActionResponseDto> implements
        SetAdministrativeStatusBundleCommandExecutor {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SetAdministrativeStatusBundleCommandExecutorImpl.class);
    private static final String WAS_NOT_SUCCESSFUL = " was not successful";
    private static final String SET_ADMINISTRATIVE_STATUS_TO = "Set administrative status to ";

    @Autowired
    private SetAdministrativeStatusCommandExecutor setAdministrativeStatusCommandExecutor;

    public SetAdministrativeStatusBundleCommandExecutorImpl() {
        super(AdministrativeStatusTypeDataDto.class);
    }

    @Override
    public ActionResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final AdministrativeStatusTypeDataDto administrativeStatusType) {

        final AdministrativeStatusTypeDto adminStatusType = administrativeStatusType.getAdministrativeStatusType();

        try {

            final AccessResultCode resultCode = this.setAdministrativeStatusCommandExecutor.execute(conn, device,
                    adminStatusType);
            if (AccessResultCode.SUCCESS.equals(resultCode)) {
                return new ActionResponseDto(SET_ADMINISTRATIVE_STATUS_TO + adminStatusType + " was successful");
            } else {
                return new ActionResponseDto(SET_ADMINISTRATIVE_STATUS_TO + adminStatusType
                        + " was not successful. Result code: " + resultCode);
            }
        } catch (final ProtocolAdapterException e) {
            LOGGER.error(SET_ADMINISTRATIVE_STATUS_TO + adminStatusType + WAS_NOT_SUCCESSFUL, e);
            return new ActionResponseDto(e, SET_ADMINISTRATIVE_STATUS_TO + adminStatusType + WAS_NOT_SUCCESSFUL);
        }
    }

    public SetAdministrativeStatusCommandExecutor getSetAdministrativeStatusCommandExecutor() {
        return this.setAdministrativeStatusCommandExecutor;
    }

    public void setSetAdministrativeStatusCommandExecutor(
            final SetAdministrativeStatusCommandExecutor setAdministrativeStatusCommandExecutor) {
        this.setAdministrativeStatusCommandExecutor = setAdministrativeStatusCommandExecutor;
    }
}
