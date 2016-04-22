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
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQueryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;

@Component()
public class GetPeriodicMeterReadsBundleCommandExecutorImpl implements GetPeriodicMeterReadsBundleCommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPeriodicMeterReadsBundleCommandExecutorImpl.class);

    @Autowired
    GetPeriodicMeterReadsCommandExecutor getPeriodicMeterReadsCommandExecutor;

    @Override
    public ActionResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final PeriodicMeterReadsRequestDataDto periodicMeterReadsRequestDataDto) {

        final PeriodicMeterReadsQueryDto periodicMeterReadsQueryDto = new PeriodicMeterReadsQueryDto(
                periodicMeterReadsRequestDataDto.getPeriodType(), periodicMeterReadsRequestDataDto.getBeginDate(),
                periodicMeterReadsRequestDataDto.getEndDate());

        try {
            return this.getPeriodicMeterReadsCommandExecutor.execute(conn, device, periodicMeterReadsQueryDto);
        } catch (final ProtocolAdapterException e) {
            LOGGER.error("Error while getting periodic meter reads from device: " + device.getDeviceIdentification());
            return new ActionResponseDto(e, "Error while getting periodic meter reads from device: "
                    + device.getDeviceIdentification());
        }
    }

    public GetPeriodicMeterReadsCommandExecutor getGetPeriodicMeterReadsCommandExecutor() {
        return getPeriodicMeterReadsCommandExecutor;
    }

    public void setGetPeriodicMeterReadsCommandExecutor(
            GetPeriodicMeterReadsCommandExecutor getPeriodicMeterReadsCommandExecutor) {
        this.getPeriodicMeterReadsCommandExecutor = getPeriodicMeterReadsCommandExecutor;
    }
}
