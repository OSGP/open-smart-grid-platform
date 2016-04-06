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

import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGasDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGasRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQueryDto;

@Component()
public class GetPeriodicMeterReadsGasBundleCommandExecutor implements
        CommandExecutor<PeriodicMeterReadsGasRequestDataDto, PeriodicMeterReadsContainerGasDto> {

    @Autowired
    GetPeriodicMeterReadsGasCommandExecutor getPeriodicMeterReadsGasCommandExecutor;

    @Override
    public PeriodicMeterReadsContainerGasDto execute(final ClientConnection conn, final DlmsDevice device,
            final PeriodicMeterReadsGasRequestDataDto periodicMeterReadsGasRequestDataDto)
            throws ProtocolAdapterException {

        final PeriodicMeterReadsQueryDto periodicMeterReadsQueryDto = new PeriodicMeterReadsQueryDto(
                periodicMeterReadsGasRequestDataDto.getPeriodType(),
                periodicMeterReadsGasRequestDataDto.getBeginDate(), periodicMeterReadsGasRequestDataDto.getEndDate(),
                periodicMeterReadsGasRequestDataDto.getChannel());

        return this.getPeriodicMeterReadsGasCommandExecutor.execute(conn, device, periodicMeterReadsQueryDto);

    }
}
