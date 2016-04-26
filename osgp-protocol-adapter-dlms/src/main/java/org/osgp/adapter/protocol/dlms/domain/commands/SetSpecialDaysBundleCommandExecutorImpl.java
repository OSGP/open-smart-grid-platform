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
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;

@Component()
public class SetSpecialDaysBundleCommandExecutorImpl extends
        BundleCommandExecutor<SpecialDaysRequestDataDto, ActionResponseDto> implements
        SetSpecialDaysBundleCommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetSpecialDaysBundleCommandExecutorImpl.class);

    @Autowired
    SetSpecialDaysCommandExecutor setSpecialDaysCommandExecutor;

    public SetSpecialDaysBundleCommandExecutorImpl() {
        super(SpecialDaysRequestDataDto.class);
    }

    @Override
    public ActionResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final SpecialDaysRequestDataDto specialDaysRequestDataDto) {

        try {
            final AccessResultCode resultCode = this.setSpecialDaysCommandExecutor.execute(conn, device,
                    specialDaysRequestDataDto.getSpecialDays());
            if (AccessResultCode.SUCCESS.equals(resultCode)) {
                return new ActionResponseDto("Set special days was successful");
            } else {
                return new ActionResponseDto("Set special days was not successful. Result code: " + resultCode);
            }
        } catch (final ProtocolAdapterException e) {
            LOGGER.error("Set special days was not successful", e);
            return new ActionResponseDto(e, "Set special days was not successful");
        }
    }

    public SetSpecialDaysCommandExecutor getSetSpecialDaysCommandExecutor() {
        return this.setSpecialDaysCommandExecutor;
    }

    public void setSetSpecialDaysCommandExecutor(final SetSpecialDaysCommandExecutor setSpecialDaysCommandExecutor) {
        this.setSpecialDaysCommandExecutor = setSpecialDaysCommandExecutor;
    }
}
