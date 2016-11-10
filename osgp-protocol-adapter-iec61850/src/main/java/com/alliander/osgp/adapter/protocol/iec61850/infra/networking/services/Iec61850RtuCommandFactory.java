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

import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuReadCommandFactory;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850AlarmCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850AlarmOtherCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850BehaviourCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850HealthCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ModeCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ScheduleCatCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ScheduleIdCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ScheduleTypeCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850WarningCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850WarningOtherCommand;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementFilterDto;

public final class Iec61850RtuCommandFactory implements RtuReadCommandFactory<MeasurementDto, MeasurementFilterDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850RtuCommandFactory.class);
    private static final int SCHEDULE_ID_START = 1;
    private static final int SCHEDULE_ID_END = 4;

    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;

    private static Iec61850RtuCommandFactory instance;

    private Map<String, RtuReadCommand<MeasurementDto>> rtuCommandMap = new HashMap<>();

    private Iec61850RtuCommandFactory() {
        this.rtuCommandMap.put(DataAttribute.BEHAVIOR.getDescription(), new Iec61850BehaviourCommand());
        this.rtuCommandMap.put(DataAttribute.HEALTH.getDescription(), new Iec61850HealthCommand());
        this.rtuCommandMap.put(DataAttribute.MODE.getDescription(), new Iec61850ModeCommand());
        this.rtuCommandMap.put(DataAttribute.ALARM_ONE.getDescription(), new Iec61850AlarmCommand(ONE));
        this.rtuCommandMap.put(DataAttribute.ALARM_TWO.getDescription(), new Iec61850AlarmCommand(TWO));
        this.rtuCommandMap.put(DataAttribute.ALARM_THREE.getDescription(), new Iec61850AlarmCommand(THREE));
        this.rtuCommandMap.put(DataAttribute.ALARM_FOUR.getDescription(), new Iec61850AlarmCommand(FOUR));
        this.rtuCommandMap.put(DataAttribute.ALARM_OTHER.getDescription(), new Iec61850AlarmOtherCommand());
        this.rtuCommandMap.put(DataAttribute.WARNING_ONE.getDescription(), new Iec61850WarningCommand(ONE));
        this.rtuCommandMap.put(DataAttribute.WARNING_TWO.getDescription(), new Iec61850WarningCommand(TWO));
        this.rtuCommandMap.put(DataAttribute.WARNING_THREE.getDescription(), new Iec61850WarningCommand(THREE));
        this.rtuCommandMap.put(DataAttribute.WARNING_FOUR.getDescription(), new Iec61850WarningCommand(FOUR));
        this.rtuCommandMap.put(DataAttribute.WARNING_OTHER.getDescription(), new Iec61850WarningOtherCommand());

        for (int i = SCHEDULE_ID_START; i <= SCHEDULE_ID_END; i++) {
            this.rtuCommandMap.put(DataAttribute.SCHEDULE_ID.getDescription() + i, new Iec61850ScheduleIdCommand(i));
            this.rtuCommandMap.put(DataAttribute.SCHEDULE_CAT.getDescription() + i, new Iec61850ScheduleCatCommand(i));
            this.rtuCommandMap.put(DataAttribute.SCHEDULE_CAT_RTU.getDescription() + i,
                    new Iec61850ScheduleCatCommand(i));
            this.rtuCommandMap.put(DataAttribute.SCHEDULE_TYPE.getDescription() + i,
                    new Iec61850ScheduleTypeCommand(i));
        }
    }

    public static synchronized Iec61850RtuCommandFactory getInstance() {
        if (instance == null) {
            instance = new Iec61850RtuCommandFactory();
        }
        return instance;
    }

    @Override
    public RtuReadCommand<MeasurementDto> getCommand(final MeasurementFilterDto filter) {
        final DataAttribute da = DataAttribute.fromString(filter.getNode());
        if (this.useFilterId(da)) {
            return this.getCommand(filter.getNode() + filter.getId());
        } else {
            return this.getCommand(filter.getNode());
        }
    }

    @Override
    public RtuReadCommand<MeasurementDto> getCommand(final String node) {
        final RtuReadCommand<MeasurementDto> command = this.rtuCommandMap.get(node);

        if (command == null) {
            LOGGER.warn("No command found for node {}", node);
        }
        return command;
    }

    private boolean useFilterId(final DataAttribute da) {
        boolean result = false;
        if (da == DataAttribute.SCHEDULE_ID || da == DataAttribute.SCHEDULE_TYPE) {
            result = true;
        } 
        if (da == DataAttribute.SCHEDULE_CAT || da == DataAttribute.SCHEDULE_CAT_RTU) {
            result = true;
        } 
        if (da == DataAttribute.SCHEDULE_ABS_TIME) {
            result = true;
        }
        return result;
    }

}
