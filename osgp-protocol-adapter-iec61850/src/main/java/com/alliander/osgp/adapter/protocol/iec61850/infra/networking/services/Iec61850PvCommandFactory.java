/**
 * Copyright 2016 Smart Society Services B.V.
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
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuReadCommandFactory;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ActualPowerCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ActualPowerLimitCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850AlarmCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850AlarmOtherCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850BehaviourCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850HealthCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850MaximumActualPowerCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850MinimumActualPowerCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ModeCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850OperationalHoursCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850StateCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850TotalEnergyCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850WarningCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850WarningOtherCommand;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementFilterDto;

@Component
public final class Iec61850PvCommandFactory implements RtuReadCommandFactory<MeasurementDto, MeasurementFilterDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850PvCommandFactory.class);

    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;

    private static final Map<DataAttribute, RtuReadCommand<MeasurementDto>> RTU_COMMAND_MAP = new HashMap<>();

    static {
        initializeRtuCommandMap();
    }

    @Override
    public RtuReadCommand<MeasurementDto> getCommand(final MeasurementFilterDto filter) {
        return this.getCommand(DataAttribute.fromString(filter.getNode()));
    }

    @Override
    public RtuReadCommand<MeasurementDto> getCommand(final String node) {
        return this.getCommand(DataAttribute.fromString(node));
    }

    private static void initializeRtuCommandMap() {
        RTU_COMMAND_MAP.put(DataAttribute.BEHAVIOR, new Iec61850BehaviourCommand());
        RTU_COMMAND_MAP.put(DataAttribute.HEALTH, new Iec61850HealthCommand());
        RTU_COMMAND_MAP.put(DataAttribute.OPERATIONAL_HOURS, new Iec61850OperationalHoursCommand());
        RTU_COMMAND_MAP.put(DataAttribute.MODE, new Iec61850ModeCommand());
        RTU_COMMAND_MAP.put(DataAttribute.ACTUAL_POWER, new Iec61850ActualPowerCommand());
        RTU_COMMAND_MAP.put(DataAttribute.MAX_ACTUAL_POWER, new Iec61850MaximumActualPowerCommand());
        RTU_COMMAND_MAP.put(DataAttribute.MIN_ACTUAL_POWER, new Iec61850MinimumActualPowerCommand());
        RTU_COMMAND_MAP.put(DataAttribute.ACTUAL_POWER_LIMIT, new Iec61850ActualPowerLimitCommand());
        RTU_COMMAND_MAP.put(DataAttribute.TOTAL_ENERGY, new Iec61850TotalEnergyCommand());
        RTU_COMMAND_MAP.put(DataAttribute.STATE, new Iec61850StateCommand());
        RTU_COMMAND_MAP.put(DataAttribute.ALARM_ONE, new Iec61850AlarmCommand(ONE));
        RTU_COMMAND_MAP.put(DataAttribute.ALARM_TWO, new Iec61850AlarmCommand(TWO));
        RTU_COMMAND_MAP.put(DataAttribute.ALARM_THREE, new Iec61850AlarmCommand(THREE));
        RTU_COMMAND_MAP.put(DataAttribute.ALARM_FOUR, new Iec61850AlarmCommand(FOUR));
        RTU_COMMAND_MAP.put(DataAttribute.ALARM_OTHER, new Iec61850AlarmOtherCommand());
        RTU_COMMAND_MAP.put(DataAttribute.WARNING_ONE, new Iec61850WarningCommand(ONE));
        RTU_COMMAND_MAP.put(DataAttribute.WARNING_TWO, new Iec61850WarningCommand(TWO));
        RTU_COMMAND_MAP.put(DataAttribute.WARNING_THREE, new Iec61850WarningCommand(THREE));
        RTU_COMMAND_MAP.put(DataAttribute.WARNING_FOUR, new Iec61850WarningCommand(FOUR));
        RTU_COMMAND_MAP.put(DataAttribute.WARNING_OTHER, new Iec61850WarningOtherCommand());
    }

    private RtuReadCommand<MeasurementDto> getCommand(final DataAttribute dataAttribute) {

        final RtuReadCommand<MeasurementDto> command = RTU_COMMAND_MAP.get(dataAttribute);

        if (command == null) {
            LOGGER.warn("No command found for data attribute {}", dataAttribute);
        }
        return command;
    }
}
