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
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;

@Component()
public class GetActualMeterReadsBundleCommandExecutorImpl implements GetActualMeterReadsBundleCommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetActualMeterReadsBundleCommandExecutorImpl.class);

    @Autowired
    GetActualMeterReadsCommandExecutor getActualMeterReadsCommandExecutor;

    @Override
    public ActionResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final ActualMeterReadsDataDto actualMeterReadsDataDto) {

        final ActualMeterReadsQueryDto actualMeterReadsQuery = new ActualMeterReadsQueryDto();

        try {
            return this.getActualMeterReadsCommandExecutor.execute(conn, device, actualMeterReadsQuery);
        } catch (final ProtocolAdapterException e) {
            LOGGER.error("Error while getting actual meter reads from device: " + device.getDeviceIdentification());
            return new ActionResponseDto(e, "Error while getting actual meter reads from device: "
                    + device.getDeviceIdentification());
        }

    }

    public GetActualMeterReadsCommandExecutor getGetActualMeterReadsCommandExecutor() {
        return getActualMeterReadsCommandExecutor;
    }

    public void setGetActualMeterReadsCommandExecutor(GetActualMeterReadsCommandExecutor getActualMeterReadsCommandExecutor) {
        this.getActualMeterReadsCommandExecutor = getActualMeterReadsCommandExecutor;
    }
    
     
}
