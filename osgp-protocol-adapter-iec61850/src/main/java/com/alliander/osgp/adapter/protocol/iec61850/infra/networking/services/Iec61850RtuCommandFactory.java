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

import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuCommand;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuCommandFactory;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850AlarmCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850AlarmOtherCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850BehaviourCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850HealthCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ModeCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850WarningCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850WarningOtherCommand;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementFilterDto;

public class Iec61850RtuCommandFactory implements RtuCommandFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850RtuCommandFactory.class);

    private static Iec61850RtuCommandFactory instance;

    private Map<DataAttribute, RtuCommand> rtuCommandMap = new HashMap<>();

    private Iec61850RtuCommandFactory() {
        this.rtuCommandMap.put(DataAttribute.BEHAVIOR, new Iec61850BehaviourCommand());
        this.rtuCommandMap.put(DataAttribute.HEALTH, new Iec61850HealthCommand());
        this.rtuCommandMap.put(DataAttribute.MODE, new Iec61850ModeCommand());
        this.rtuCommandMap.put(DataAttribute.ALARM_ONE, new Iec61850AlarmCommand(1));
        this.rtuCommandMap.put(DataAttribute.ALARM_TWO, new Iec61850AlarmCommand(2));
        this.rtuCommandMap.put(DataAttribute.ALARM_THREE, new Iec61850AlarmCommand(3));
        this.rtuCommandMap.put(DataAttribute.ALARM_FOUR, new Iec61850AlarmCommand(4));
        this.rtuCommandMap.put(DataAttribute.ALARM_OTHER, new Iec61850AlarmOtherCommand());
        this.rtuCommandMap.put(DataAttribute.WARNING_ONE, new Iec61850WarningCommand(1));
        this.rtuCommandMap.put(DataAttribute.WARNING_TWO, new Iec61850WarningCommand(2));
        this.rtuCommandMap.put(DataAttribute.WARNING_THREE, new Iec61850WarningCommand(3));
        this.rtuCommandMap.put(DataAttribute.WARNING_FOUR, new Iec61850WarningCommand(4));
        this.rtuCommandMap.put(DataAttribute.WARNING_OTHER, new Iec61850WarningOtherCommand());
    }

    public static Iec61850RtuCommandFactory getInstance() {
        if (instance == null) {
            instance = new Iec61850RtuCommandFactory();
        }
        return instance;
    }

    @Override
    public RtuCommand getCommand(final MeasurementFilterDto filter) {
        return this.getCommand(DataAttribute.fromString(filter.getNode()));
    }

    @Override
    public RtuCommand getCommand(final String node) {
        return this.getCommand(DataAttribute.fromString(node));
    }

    private RtuCommand getCommand(final DataAttribute dataAttribute) {

        final RtuCommand command = this.rtuCommandMap.get(dataAttribute);

        if (command == null) {
            LOGGER.warn("No command found for data attribute {}", dataAttribute);
        }
        return command;
    }

}
