/**
 * Copyright 2016 Smart Society Services B.V.
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

import com.alliander.osgp.adapter.protocol.iec61850.application.config.BeanUtil;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850GasFurnaceCommandFactory;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;

public class Iec61850GasFurnaceReportHandler implements Iec61850ReportHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850GasFurnaceReportHandler.class);

    private static final String SYSTEM_TYPE = "GAS_FURNACE";
    private static final List<String> NODES_USING_ID_LIST = new ArrayList<>();

    static {
        intializeNodesUsingIdList();
    }

    private final int systemId;
    private final Iec61850GasFurnaceCommandFactory iec61850GasFurnaceCommandFactory;

    public Iec61850GasFurnaceReportHandler(final int systemId) {
        this.systemId = systemId;
        this.iec61850GasFurnaceCommandFactory = BeanUtil.getBean(Iec61850GasFurnaceCommandFactory.class);
    }

    @Override
    public GetDataSystemIdentifierDto createResult(final List<MeasurementDto> measurements) {
        final GetDataSystemIdentifierDto systemResult = new GetDataSystemIdentifierDto(this.systemId, SYSTEM_TYPE,
                measurements);
        final List<GetDataSystemIdentifierDto> systems = new ArrayList<>();
        systems.add(systemResult);
        return systemResult;
    }

    @Override
    public List<MeasurementDto> handleMember(final ReadOnlyNodeContainer member) {

        final List<MeasurementDto> measurements = new ArrayList<>();
        final RtuReadCommand<MeasurementDto> command = this.iec61850GasFurnaceCommandFactory
                .getCommand(this.getCommandName(member));

        if (command == null) {
            LOGGER.warn("No command found for node {}", member.getFcmodelNode().getName());
        } else {
            measurements.add(command.translate(member));
        }
        return measurements;
    }

    private static void intializeNodesUsingIdList() {
        NODES_USING_ID_LIST.add(DataAttribute.TEMPERATURE.getDescription());
        NODES_USING_ID_LIST.add(DataAttribute.MATERIAL_FLOW.getDescription());
        NODES_USING_ID_LIST.add(DataAttribute.MATERIAL_STATUS.getDescription());
        NODES_USING_ID_LIST.add(DataAttribute.MATERIAL_TYPE.getDescription());
    }

    private static boolean useId(final String nodeName) {
        return NODES_USING_ID_LIST.contains(nodeName);
    }

    private String getCommandName(final ReadOnlyNodeContainer member) {
        final String nodeName = member.getFcmodelNode().getName();
        if (useId(nodeName)) {
            final String refName = member.getFcmodelNode().getReference().toString();
            final int startIndex = refName.length() - nodeName.length() - 2;
            return nodeName + refName.substring(startIndex, startIndex + 1);
        } else {
            return nodeName;
        }
    }

}
