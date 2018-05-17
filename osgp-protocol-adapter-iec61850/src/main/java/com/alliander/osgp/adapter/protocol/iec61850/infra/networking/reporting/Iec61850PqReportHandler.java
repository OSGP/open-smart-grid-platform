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
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850PqCommandFactory;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;

public class Iec61850PqReportHandler implements Iec61850ReportHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850PqReportHandler.class);

    private static final String SYSTEM_TYPE = "PQ";

    private static final Set<String> COMPOSITE_NODES = new HashSet<>(Arrays.asList(new String[] { "PNV", "PF", "Z" }));

    private static final Set<DataAttribute> NODES_USING_ID = EnumSet.of(DataAttribute.FREQUENCY,
            DataAttribute.PHASE_TO_NEUTRAL_VOLTAGE_PHASE_A, DataAttribute.PHASE_TO_NEUTRAL_VOLTAGE_PHASE_B,
            DataAttribute.PHASE_TO_NEUTRAL_VOLTAGE_PHASE_C, DataAttribute.POWER_FACTOR_PHASE_A,
            DataAttribute.POWER_FACTOR_PHASE_B, DataAttribute.POWER_FACTOR_PHASE_C, DataAttribute.IMPEDANCE_PHASE_A,
            DataAttribute.IMPEDANCE_PHASE_B, DataAttribute.IMPEDANCE_PHASE_C, DataAttribute.VOLTAGE_DIPS);

    private static final Pattern NODE_PATTERN = Pattern
            .compile("\\A(.*)PQ([1-9]\\d*+)/(LLN0|DRCC|DGEN|MMXU|GGIO|QVVR)([1-9]\\d*+)?\\.(.*)\\Z");

    private static final Iec61850ReportNodeHelper NODE_HELPER = new Iec61850ReportNodeHelper(NODES_USING_ID,
            NODE_PATTERN, 4);

    private final int systemId;
    private final Iec61850PqCommandFactory iec61850PqCommandFactory;

    public Iec61850PqReportHandler(final int systemId) {
        this.systemId = systemId;
        this.iec61850PqCommandFactory = BeanUtil.getBean(Iec61850PqCommandFactory.class);
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

                final RtuReadCommand<MeasurementDto> command = this.iec61850PqCommandFactory
                        .getCommand(NODE_HELPER.getChildCommandName(child));

                if (command == null) {
                    LOGGER.warn("No command found for node {}", child.getReference().getName());
                } else {
                    measurements.add(command.translate(member.getChild(child.getName())));
                }
            }
        } else {
            final RtuReadCommand<MeasurementDto> command = this.iec61850PqCommandFactory
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