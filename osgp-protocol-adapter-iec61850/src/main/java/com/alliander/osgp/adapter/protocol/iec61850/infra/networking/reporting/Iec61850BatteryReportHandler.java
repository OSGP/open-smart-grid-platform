/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850BatteryCommandFactory;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementResultSystemIdentifierDto;

public class Iec61850BatteryReportHandler implements Iec61850ReportHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850BatteryReportHandler.class);

    private static String SYSTEM_TYPE = "BATTERY";

    private int systemId;

    public Iec61850BatteryReportHandler(final int systemId) {
        this.systemId = systemId;
    }

    @Override
    public MeasurementResultSystemIdentifierDto createResult(final List<MeasurementDto> measurements) {
        final MeasurementResultSystemIdentifierDto systemResult = new MeasurementResultSystemIdentifierDto(
                this.systemId, SYSTEM_TYPE, measurements);
        final List<MeasurementResultSystemIdentifierDto> systems = new ArrayList<>();
        systems.add(systemResult);
        return systemResult;
    }

    @Override
    public MeasurementDto handleMember(final ReadOnlyNodeContainer member) {

        final RtuCommand command = Iec61850BatteryCommandFactory.getInstance().getCommand(
                member.getFcmodelNode().getName());

        if (command == null) {
            LOGGER.warn("No command found for node {}", member.getFcmodelNode().getName());
            return null;
        } else {
            return command.translate(member);
        }
    }
}
