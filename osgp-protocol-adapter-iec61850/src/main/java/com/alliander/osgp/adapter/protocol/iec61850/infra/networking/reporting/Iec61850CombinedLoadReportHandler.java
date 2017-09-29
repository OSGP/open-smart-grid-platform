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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.application.config.BeanUtil;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850CombinedLoadCommandFactory;
import com.alliander.osgp.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;

/**
 *
 * @deprecated, the structure of multiple mmxu/mmtr nodes within a single load
 * device is replaced by multiple load devices with single mmxu/mmtr nodes. This
 * code should be removed when all rtu devices are using the new structure
 *
 */
@Deprecated
public class Iec61850CombinedLoadReportHandler implements Iec61850ReportHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850CombinedLoadReportHandler.class);

    private static final String SYSTEM_TYPE = "LOAD";
    private static final List<String> NODES_USING_ID_LIST = new ArrayList<>();

    private static final Pattern NODE_PATTERN = Pattern
            .compile("\\A(.*)LOAD([1-9]\\d*+)/(LLN0|MMTR|MMXU|GGIO)([1-9]\\d*+)?\\.(.*)\\Z");

    static {
        intializeNodesUsingIdList();
    }

    private int systemId;
    private Iec61850CombinedLoadCommandFactory iec61850CombinedLoadCommandFactory;

    public Iec61850CombinedLoadReportHandler(final int systemId) {
        this.systemId = systemId;
        this.iec61850CombinedLoadCommandFactory = BeanUtil.getBean(Iec61850CombinedLoadCommandFactory.class);
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

        final RtuReadCommand<MeasurementDto> command = this.iec61850CombinedLoadCommandFactory
                .getCommand(this.getCommandName(member));

        if (command == null) {
            LOGGER.warn("No command found for node {}", member.getFcmodelNode().getName());
        } else {
            measurements.add(command.translate(member));
        }
        return measurements;
    }

    private static void intializeNodesUsingIdList() {
        NODES_USING_ID_LIST.add("TotWh");
        NODES_USING_ID_LIST.add("TotW");
        NODES_USING_ID_LIST.add("MaxWPhs");
        NODES_USING_ID_LIST.add("MinWPhs");
    }

    private static boolean useId(final String nodeName) {
        return NODES_USING_ID_LIST.contains(nodeName);
    }

    private String getCommandName(final ReadOnlyNodeContainer member) {

        final String nodeName = member.getFcmodelNode().getName();
        if (useId(nodeName)) {
            final String reference = member.getFcmodelNode().getReference().toString();
            return nodeName + this.getIndex(reference);
        } else {
            return nodeName;
        }
    }

    private String getIndex(final String reference) {
        String index = "";
        final Matcher reportMatcher = NODE_PATTERN.matcher(reference);
        if (reportMatcher.matches()) {
            index = reportMatcher.group(4);
        }
        return index;
    }

}
