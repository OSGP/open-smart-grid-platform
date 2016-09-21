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
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850BehaviourCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850HealthCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850LoadActualPowerCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850LoadTotalEnergyCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850ModeCommand;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementFilterDto;

public class Iec61850LoadCommandFactory implements RtuCommandFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850LoadCommandFactory.class);

    private static final int ID_START = 1;
    private static final int ID_END = 5;

    private static Iec61850LoadCommandFactory instance;

    private Map<String, RtuCommand> rtuCommandMap = new HashMap<>();

    private Iec61850LoadCommandFactory() {
        this.rtuCommandMap.put(DataAttribute.BEHAVIOR.getDescription(), new Iec61850BehaviourCommand());
        this.rtuCommandMap.put(DataAttribute.HEALTH.getDescription(), new Iec61850HealthCommand());
        this.rtuCommandMap.put(DataAttribute.MODE.getDescription(), new Iec61850ModeCommand());

        for (int i = ID_START; i <= ID_END; i++) {
            this.rtuCommandMap.put(DataAttribute.ACTUAL_POWER.getDescription() + i,
                    new Iec61850LoadActualPowerCommand(i));
            this.rtuCommandMap.put(DataAttribute.TOTAL_ENERGY.getDescription() + i,
                    new Iec61850LoadTotalEnergyCommand(i));
        }
    }

    public static Iec61850LoadCommandFactory getInstance() {
        if (instance == null) {
            instance = new Iec61850LoadCommandFactory();
        }
        return instance;
    }

    @Override
    public RtuCommand getCommand(final MeasurementFilterDto filter) {
        final DataAttribute da = DataAttribute.fromString(filter.getNode());
        if (this.useFilterId(da)) {
            return this.getCommand(filter.getNode() + filter.getId());
        } else {
            return this.getCommand(filter.getNode());
        }
    }

    @Override
    public RtuCommand getCommand(final String node) {
        final RtuCommand command = this.rtuCommandMap.get(node);

        if (command == null) {
            LOGGER.warn("No command found for node {}", node);
        }
        return command;
    }

    private boolean useFilterId(final DataAttribute da) {
        return da != DataAttribute.BEHAVIOR && da != DataAttribute.HEALTH && da != DataAttribute.MODE;
    }
}
