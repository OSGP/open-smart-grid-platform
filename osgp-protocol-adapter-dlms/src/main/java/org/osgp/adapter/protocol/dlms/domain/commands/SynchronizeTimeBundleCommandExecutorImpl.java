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
import com.alliander.osgp.dto.valueobjects.smartmetering.SynchronizeTimeRequestDto;

@Component()
public class SynchronizeTimeBundleCommandExecutorImpl extends
        BundleCommandExecutor<SynchronizeTimeRequestDto, ActionResponseDto> implements
        SynchronizeTimeBundleCommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizeTimeBundleCommandExecutorImpl.class);

    private static final String ERROR_WHILE_SYNCHRONIZING_TIME = "Error while synchronizing time for device: ";

    @Autowired
    private SynchronizeTimeCommandExecutor synchronizeTimeCommandExecutor;

    public SynchronizeTimeBundleCommandExecutorImpl() {
        super(SynchronizeTimeRequestDto.class);
    }

    @Override
    public ActionResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final SynchronizeTimeRequestDto synchronizeTimeRequestDataDto) {

        AccessResultCode accessResultCode;
        try {
            accessResultCode = this.synchronizeTimeCommandExecutor.execute(conn, device, null);

            if (AccessResultCode.SUCCESS.equals(accessResultCode)) {
                return new ActionResponseDto("Synchronizing time for device: " + device.getDeviceIdentification()
                        + " was successful");
            } else {
                return new ActionResponseDto("Synchronizing time for device: " + device.getDeviceIdentification()
                        + " was not successful. Resultcode: " + accessResultCode);
            }

        } catch (final ProtocolAdapterException e) {
            LOGGER.error(ERROR_WHILE_SYNCHRONIZING_TIME + device.getDeviceIdentification(), e);
            return new ActionResponseDto(e, ERROR_WHILE_SYNCHRONIZING_TIME + device.getDeviceIdentification());
        }
    }

    public void setSynchronizeTimeCommandExecutor(final SynchronizeTimeCommandExecutor synchronizeTimeCommandExecutor) {
        this.synchronizeTimeCommandExecutor = synchronizeTimeCommandExecutor;
    }

}
