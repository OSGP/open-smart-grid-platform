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

import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmRegisterDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequestDto;

@Component
public class ReadAlarmRegisterBundleCommandExecutor implements
        CommandExecutor<ReadAlarmRegisterDataDto, AlarmRegisterDto> {

    @Autowired
    private ReadAlarmRegisterCommandExecutor readAlarmRegisterCommandExecutor;

    @Override
    public AlarmRegisterDto execute(final ClientConnection conn, final DlmsDevice device,
            final ReadAlarmRegisterDataDto object) throws ProtocolAdapterException {

        final ReadAlarmRegisterRequestDto readAlarmRegisterRequestDto = new ReadAlarmRegisterRequestDto("not relevant");

        return this.readAlarmRegisterCommandExecutor.execute(conn, device, readAlarmRegisterRequestDto);

    }
}
