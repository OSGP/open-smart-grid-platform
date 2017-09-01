/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmuc.openiec61850.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850WindCommandFactory;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;

public class Iec61850WindReportHandler implements Iec61850ReportHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850WindReportHandler.class);

    private static final String SYSTEM_TYPE = "WIND";

    private static final List<String> COMPOSITE_NODES = Arrays.asList(new String[] { "W" });

    private int systemId;

    public Iec61850WindReportHandler(final int systemId) {
        this.systemId = systemId;
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

        if (this.isCompositeNode(member)) {
            for (final ModelNode child : member.getFcmodelNode().getChildren()) {
                final String compositeNodeName = this.getCompositeNodeName(child);

                final RtuReadCommand<MeasurementDto> command = Iec61850WindCommandFactory.getInstance()
                        .getCommand(compositeNodeName);

                if (command == null) {
                    LOGGER.warn("No command found for node {}", compositeNodeName);
                } else {
                    measurements.add(command.translate(member.getChild(child.getName())));
                }
            }
        } else {
            final RtuReadCommand<MeasurementDto> command = Iec61850WindCommandFactory.getInstance()
                    .getCommand(member.getFcmodelNode().getName());

            if (command == null) {
                LOGGER.warn("No command found for node {}", member.getFcmodelNode().getName());
            } else {
                measurements.add(command.translate(member));
            }
        }
        return measurements;
    }

    private boolean isCompositeNode(final ReadOnlyNodeContainer member) {
        return COMPOSITE_NODES.contains(member.getFcmodelNode().getName());
    }

    private String getCompositeNodeName(final ModelNode childNode) {
        return childNode.getParent().getName() + "." + childNode.getName();
    }

}
