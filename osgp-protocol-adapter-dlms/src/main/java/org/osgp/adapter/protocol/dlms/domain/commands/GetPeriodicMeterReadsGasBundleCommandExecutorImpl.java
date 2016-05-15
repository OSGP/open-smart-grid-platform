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
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGasRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;

@Component()
public class GetPeriodicMeterReadsGasBundleCommandExecutorImpl extends
BundleCommandExecutor<PeriodicMeterReadsGasRequestDto, ActionResponseDto> implements
GetPeriodicMeterReadsGasBundleCommandExecutor {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(GetPeriodicMeterReadsGasBundleCommandExecutorImpl.class);

    @Autowired
    GetPeriodicMeterReadsGasCommandExecutor getPeriodicMeterReadsGasCommandExecutor;

    public GetPeriodicMeterReadsGasBundleCommandExecutorImpl() {
        super(PeriodicMeterReadsGasRequestDto.class);
    }

    @Override
    public ActionResponseDto execute(final ClientConnection conn, final DlmsDevice device,
            final PeriodicMeterReadsGasRequestDto periodicMeterReadsGasRequestDataDto) {

        final PeriodicMeterReadsRequestDto periodicMeterReadsQueryDto = new PeriodicMeterReadsRequestDto(
                periodicMeterReadsGasRequestDataDto.getPeriodType(),
                periodicMeterReadsGasRequestDataDto.getBeginDate(), periodicMeterReadsGasRequestDataDto.getEndDate(),
                periodicMeterReadsGasRequestDataDto.getChannel());

        try {
            return this.getPeriodicMeterReadsGasCommandExecutor.execute(conn, device, periodicMeterReadsQueryDto);
        } catch (final ProtocolAdapterException e) {
            LOGGER.error(
                    "Error while getting periodic meter reads gas from device: " + device.getDeviceIdentification(), e);
            return new ActionResponseDto(e, "Error while getting periodic meter reads gas from device: "
                    + device.getDeviceIdentification());
        }
    }

    public GetPeriodicMeterReadsGasCommandExecutor getGetPeriodicMeterReadsGasCommandExecutor() {
        return this.getPeriodicMeterReadsGasCommandExecutor;
    }

    public void setGetPeriodicMeterReadsGasCommandExecutor(
            final GetPeriodicMeterReadsGasCommandExecutor getPeriodicMeterReadsGasCommandExecutor) {
        this.getPeriodicMeterReadsGasCommandExecutor = getPeriodicMeterReadsGasCommandExecutor;
    }
}
