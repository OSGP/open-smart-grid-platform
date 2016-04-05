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

import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsDataGasDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsGasDto;

@Component()
public class GetActualMeterReadsBundleGasCommandExecutor implements
        CommandExecutor<ActualMeterReadsDataGasDto, MeterReadsGasDto> {

    @Autowired
    private GetActualMeterReadsGasCommandExecutor getActualMeterReadsGasCommandExecutor;

    @Override
    public MeterReadsGasDto execute(final ClientConnection conn, final DlmsDevice device,
            final ActualMeterReadsDataGasDto actualMeterReadsDataGasDto) throws ProtocolAdapterException {

        final ActualMeterReadsQueryDto actualMeterReadsQueryDto = new ActualMeterReadsQueryDto(
                actualMeterReadsDataGasDto.getChannel());

        return this.getActualMeterReadsGasCommandExecutor.execute(conn, device, actualMeterReadsQueryDto);

    }

}
