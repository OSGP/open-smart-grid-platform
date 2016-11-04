/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuWriteCommand;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuWriteCommandFactory;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ScheduleCatCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ScheduleIdCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ScheduleTypeCommand;
import com.alliander.osgp.dto.valueobjects.microgrids.SetPointDto;

public class Iec61850RtuSetPointCommandFactory implements RtuWriteCommandFactory<SetPointDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850RtuSetPointCommandFactory.class);

    private static Iec61850RtuSetPointCommandFactory instance;
    private static int ID_START = 1;
    private static int ID_END = 4;

    private Map<String, RtuWriteCommand<SetPointDto>> rtuCommandMap = new HashMap<>();

    private Iec61850RtuSetPointCommandFactory() {
        for (int i = ID_START; i <= ID_END; i++) {

            this.rtuCommandMap.put(this.createMapKey(DataAttribute.SCHEDULE_ID, i), new Iec61850ScheduleIdCommand(i));
            this.rtuCommandMap.put(this.createMapKey(DataAttribute.SCHEDULE_TYPE, i),
                    new Iec61850ScheduleTypeCommand(i));
            this.rtuCommandMap.put(this.createMapKey(DataAttribute.SCHEDULE_CAT, i), new Iec61850ScheduleCatCommand(i));
        }
    }

    public static Iec61850RtuSetPointCommandFactory getInstance() {
        if (instance == null) {
            instance = new Iec61850RtuSetPointCommandFactory();
        }
        return instance;
    }

    @Override
    public RtuWriteCommand<SetPointDto> getCommand(final String node) {

        final RtuWriteCommand<SetPointDto> command = this.rtuCommandMap.get(node);

        if (command == null) {
            LOGGER.warn("No command found for data attribute {}", node);
        }
        return command;
    }

    private String createMapKey(final DataAttribute da, final int index) {
        return da.getDescription() + index;
    }
}
