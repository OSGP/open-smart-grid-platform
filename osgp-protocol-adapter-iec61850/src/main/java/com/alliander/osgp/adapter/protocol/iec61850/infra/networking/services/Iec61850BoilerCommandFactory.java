/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850MaterialFlowCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850MaterialStatusCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850MaterialTypeCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850MaximumActualPowerCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850MinimumActualPowerCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ModeCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850OperationalHoursCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ScheduleCatCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ScheduleIdCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ScheduleTypeCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850StateCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850TemperatureCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850TotalEnergyCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850VlmCapCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850WarningCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850WarningOtherCommand;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementFilterDto;

@Component
public class Iec61850BoilerCommandFactory implements RtuReadCommandFactory<MeasurementDto, MeasurementFilterDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850BoilerCommandFactory.class);

    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int SCHEDULE_ID_START = 1;
    private static final int SCHEDULE_ID_END = 4;

    private static final Map<String, RtuReadCommand<MeasurementDto>> RTU_COMMAND_MAP = new HashMap<>();
    private static final Set<DataAttribute> DATA_ATTRIBUTE_USING_FILTER_ID_LIST = new HashSet<>();

    static {
        initializeRtuCommandMap();
        initializeDataAttributesUsingFilterIdList();
    }

    @Override
    public RtuReadCommand<MeasurementDto> getCommand(final MeasurementFilterDto filter) {
        final DataAttribute dataAttribute = DataAttribute.fromString(filter.getNode());
        if (this.useFilterId(dataAttribute)) {
            return this.getCommand(filter.getNode() + filter.getId());
        } else {
            return this.getCommand(filter.getNode());
        }
    }

    @Override
    public RtuReadCommand<MeasurementDto> getCommand(final String node) {
        final RtuReadCommand<MeasurementDto> command = RTU_COMMAND_MAP.get(node);

        if (command == null) {
            LOGGER.warn("No command found for node {}", node);
        }

        return command;
    }

    private boolean useFilterId(final DataAttribute dataAttribute) {
        return DATA_ATTRIBUTE_USING_FILTER_ID_LIST.contains(dataAttribute);
    }

    private static void initializeRtuCommandMap() {
        RTU_COMMAND_MAP.put(DataAttribute.HEALTH.getDescription(), new Iec61850HealthCommand());
        RTU_COMMAND_MAP.put(DataAttribute.BEHAVIOR.getDescription(), new Iec61850BehaviourCommand());
        RTU_COMMAND_MAP.put(DataAttribute.MODE.getDescription(), new Iec61850ModeCommand());

        RTU_COMMAND_MAP.put(DataAttribute.ALARM_ONE.getDescription(), new Iec61850AlarmCommand(ONE));
        RTU_COMMAND_MAP.put(DataAttribute.ALARM_TWO.getDescription(), new Iec61850AlarmCommand(TWO));
        RTU_COMMAND_MAP.put(DataAttribute.ALARM_THREE.getDescription(), new Iec61850AlarmCommand(THREE));
        RTU_COMMAND_MAP.put(DataAttribute.ALARM_FOUR.getDescription(), new Iec61850AlarmCommand(FOUR));
        RTU_COMMAND_MAP.put(DataAttribute.ALARM_OTHER.getDescription(), new Iec61850AlarmOtherCommand());

        RTU_COMMAND_MAP.put(DataAttribute.WARNING_ONE.getDescription(), new Iec61850WarningCommand(ONE));
        RTU_COMMAND_MAP.put(DataAttribute.WARNING_TWO.getDescription(), new Iec61850WarningCommand(TWO));
        RTU_COMMAND_MAP.put(DataAttribute.WARNING_THREE.getDescription(), new Iec61850WarningCommand(THREE));
        RTU_COMMAND_MAP.put(DataAttribute.WARNING_FOUR.getDescription(), new Iec61850WarningCommand(FOUR));
        RTU_COMMAND_MAP.put(DataAttribute.WARNING_OTHER.getDescription(), new Iec61850WarningOtherCommand());

        for (int i = SCHEDULE_ID_START; i <= SCHEDULE_ID_END; i++) {
            RTU_COMMAND_MAP.put(DataAttribute.SCHEDULE_ID.getDescription() + i, new Iec61850ScheduleIdCommand(i));
            RTU_COMMAND_MAP.put(DataAttribute.SCHEDULE_CAT.getDescription() + i, new Iec61850ScheduleCatCommand(i));
            RTU_COMMAND_MAP.put(DataAttribute.SCHEDULE_CAT_RTU.getDescription() + i, new Iec61850ScheduleCatCommand(i));
            RTU_COMMAND_MAP.put(DataAttribute.SCHEDULE_TYPE.getDescription() + i, new Iec61850ScheduleTypeCommand(i));
        }

        RTU_COMMAND_MAP.put(DataAttribute.ACTUAL_POWER.getDescription(), new Iec61850ActualPowerCommand());
        RTU_COMMAND_MAP.put(DataAttribute.MIN_ACTUAL_POWER.getDescription(), new Iec61850MinimumActualPowerCommand());
        RTU_COMMAND_MAP.put(DataAttribute.MAX_ACTUAL_POWER.getDescription(), new Iec61850MaximumActualPowerCommand());
        RTU_COMMAND_MAP.put(DataAttribute.ACTUAL_POWER_LIMIT.getDescription(), new Iec61850ActualPowerLimitCommand());
        RTU_COMMAND_MAP.put(DataAttribute.TOTAL_ENERGY.getDescription(), new Iec61850TotalEnergyCommand());
        RTU_COMMAND_MAP.put(DataAttribute.STATE.getDescription(), new Iec61850StateCommand());
        RTU_COMMAND_MAP.put(DataAttribute.OPERATIONAL_HOURS.getDescription(), new Iec61850OperationalHoursCommand());

        for (int i = ONE; i <= FOUR; i++) {
            RTU_COMMAND_MAP.put(DataAttribute.TEMPERATURE.getDescription() + i, new Iec61850TemperatureCommand(i));
        }

        RTU_COMMAND_MAP.put(DataAttribute.MATERIAL_STATUS.getDescription() + ONE,
                new Iec61850MaterialStatusCommand(ONE));
        RTU_COMMAND_MAP.put(DataAttribute.MATERIAL_TYPE.getDescription() + ONE, new Iec61850MaterialTypeCommand(ONE));
        RTU_COMMAND_MAP.put(DataAttribute.MATERIAL_FLOW.getDescription() + ONE, new Iec61850MaterialFlowCommand(ONE));
        RTU_COMMAND_MAP.put(DataAttribute.VLMCAP.getDescription(), new Iec61850VlmCapCommand());

    }

    private static void initializeDataAttributesUsingFilterIdList() {
        DATA_ATTRIBUTE_USING_FILTER_ID_LIST.add(DataAttribute.SCHEDULE_ID);
        DATA_ATTRIBUTE_USING_FILTER_ID_LIST.add(DataAttribute.SCHEDULE_CAT);
        DATA_ATTRIBUTE_USING_FILTER_ID_LIST.add(DataAttribute.SCHEDULE_CAT_RTU);
        DATA_ATTRIBUTE_USING_FILTER_ID_LIST.add(DataAttribute.SCHEDULE_TYPE);
        DATA_ATTRIBUTE_USING_FILTER_ID_LIST.add(DataAttribute.TEMPERATURE);
        DATA_ATTRIBUTE_USING_FILTER_ID_LIST.add(DataAttribute.MATERIAL_STATUS);
        DATA_ATTRIBUTE_USING_FILTER_ID_LIST.add(DataAttribute.MATERIAL_TYPE);
        DATA_ATTRIBUTE_USING_FILTER_ID_LIST.add(DataAttribute.MATERIAL_FLOW);
    }
}
