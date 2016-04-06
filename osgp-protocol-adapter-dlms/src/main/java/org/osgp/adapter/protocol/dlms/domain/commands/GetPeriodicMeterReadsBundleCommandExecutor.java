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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQueryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;

@Component()
public class GetPeriodicMeterReadsBundleCommandExecutor implements
CommandExecutor<PeriodicMeterReadsRequestDataDto, PeriodicMeterReadsContainerDto> {

    @Autowired
    GetPeriodicMeterReadsCommandExecutor getPeriodicMeterReadsCommandExecutor;

    @Override
    public PeriodicMeterReadsContainerDto execute(final ClientConnection conn, final DlmsDevice device,
            final PeriodicMeterReadsRequestDataDto periodicMeterReadsRequestDataDto) throws ProtocolAdapterException {

        final PeriodicMeterReadsQueryDto periodicMeterReadsQueryDto = new PeriodicMeterReadsQueryDto(
                periodicMeterReadsRequestDataDto.getPeriodType(), periodicMeterReadsRequestDataDto.getBeginDate(),
                periodicMeterReadsRequestDataDto.getEndDate());

        return this.getPeriodicMeterReadsCommandExecutor.execute(conn, device, periodicMeterReadsQueryDto);

    }
}
