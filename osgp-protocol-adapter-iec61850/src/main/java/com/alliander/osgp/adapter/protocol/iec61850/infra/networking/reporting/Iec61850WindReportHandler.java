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
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.openmuc.openiec61850.ModelNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.application.config.BeanUtil;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850WindCommandFactory;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;

public class Iec61850WindReportHandler implements Iec61850ReportHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850WindReportHandler.class);

    private static final String SYSTEM_TYPE = "WIND";
    private static final Set<String> COMPOSITE_NODES = new HashSet<>(Arrays.asList("W"));

    private static final Set<DataAttribute> NODES_USING_ID = EnumSet.of(DataAttribute.ACTIVE_POWER_PHASE_A,
            DataAttribute.ACTIVE_POWER_PHASE_B, DataAttribute.ACTIVE_POWER_PHASE_C);

    private static final Pattern NODE_PATTERN = Pattern
            .compile("\\A(.*)WIND([1-9]\\d*+)/(LLN0|DRCC|DGEN|MMXU|GGIO)([1-9]\\d*+)?\\.(.*)\\Z");

    private static final Iec61850ReportNodeHelper NODE_HELPER = new Iec61850ReportNodeHelper(NODES_USING_ID,
            NODE_PATTERN, 4);

    private final int systemId;
    private final Iec61850WindCommandFactory iec61850WindCommandFactory;

    public Iec61850WindReportHandler(final int systemId) {
        this.systemId = systemId;
        this.iec61850WindCommandFactory = BeanUtil.getBean(Iec61850WindCommandFactory.class);
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

        if (this.isCompositeNode(member.getFcmodelNode())) {
            for (final ModelNode child : member.getFcmodelNode().getChildren()) {

                final RtuReadCommand<MeasurementDto> command = this.iec61850WindCommandFactory
                        .getCommand(NODE_HELPER.getChildCommandName(child));

                if (command == null) {
                    LOGGER.warn("No command found for node {}", child.getReference().getName());
                } else {
                    measurements.add(command.translate(member.getChild(child.getName())));
                }
            }
        } else {
            final RtuReadCommand<MeasurementDto> command = this.iec61850WindCommandFactory
                    .getCommand(NODE_HELPER.getCommandName(member));

            if (command == null) {
                LOGGER.warn("No command found for node {}", member.getFcmodelNode().getReference().getName());
            } else {
                measurements.add(command.translate(member));
            }
        }
        return measurements;
    }

    private boolean isCompositeNode(final ModelNode node) {
        return COMPOSITE_NODES.contains(node.getName());
    }

}
