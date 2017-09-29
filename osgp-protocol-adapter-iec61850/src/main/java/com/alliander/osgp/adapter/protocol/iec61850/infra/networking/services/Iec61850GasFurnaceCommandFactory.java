/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuReadCommandFactory;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850AlarmCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850AlarmOtherCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850BehaviourCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850HealthCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850MaterialFlowCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850MaterialStatusCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850MaterialTypeCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ModeCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850TemperatureCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850WarningCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850WarningOtherCommand;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementFilterDto;

@Component
public class Iec61850GasFurnaceCommandFactory implements RtuReadCommandFactory<MeasurementDto, MeasurementFilterDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850GasFurnaceCommandFactory.class);

    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;

    private static final Map<String, RtuReadCommand<MeasurementDto>> RTU_COMMAND_MAP = new HashMap<>();
    private static final List<DataAttribute> DATA_ATTRIBUTE_USING_FILTER_ID_LIST = new ArrayList<>();

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
        RTU_COMMAND_MAP.put(DataAttribute.BEHAVIOR.getDescription(), new Iec61850BehaviourCommand());
        RTU_COMMAND_MAP.put(DataAttribute.HEALTH.getDescription(), new Iec61850HealthCommand());
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

        for (int i = ONE; i <= TWO; i++) {
            RTU_COMMAND_MAP.put(DataAttribute.TEMPERATURE.getDescription() + i, new Iec61850TemperatureCommand(i));
            RTU_COMMAND_MAP.put(DataAttribute.MATERIAL_STATUS.getDescription() + i,
                    new Iec61850MaterialStatusCommand(i));
            RTU_COMMAND_MAP.put(DataAttribute.MATERIAL_TYPE.getDescription() + i, new Iec61850MaterialTypeCommand(i));
            RTU_COMMAND_MAP.put(DataAttribute.MATERIAL_FLOW.getDescription() + i, new Iec61850MaterialFlowCommand(i));
        }
    }

    private static void initializeDataAttributesUsingFilterIdList() {
        DATA_ATTRIBUTE_USING_FILTER_ID_LIST.add(DataAttribute.TEMPERATURE);
        DATA_ATTRIBUTE_USING_FILTER_ID_LIST.add(DataAttribute.MATERIAL_STATUS);
        DATA_ATTRIBUTE_USING_FILTER_ID_LIST.add(DataAttribute.MATERIAL_TYPE);
        DATA_ATTRIBUTE_USING_FILTER_ID_LIST.add(DataAttribute.MATERIAL_FLOW);
    }

}
