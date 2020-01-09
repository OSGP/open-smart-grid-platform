/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.opensmartgridplatform.adapter.protocol.iec61850.application.config.BeanUtil;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.Iec61850TransformerCommandFactory;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataSystemIdentifierDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;

public class Iec61850TransformerReportHandler implements Iec61850ReportHandler {

    private static final String SYSTEM_TYPE = "TRANSFORMER";

    private static final Set<DataAttribute> NODES_USING_ID = EnumSet.of(DataAttribute.ACTIVE_POWER,
            DataAttribute.TEMPERATURE);

    private static final Pattern NODE_PATTERN = Pattern
            .compile("\\A(.*)TFR([1-9]\\d*+)/(LLN0|MMXU|TTMP)([1-9]\\d*+)?\\.(.*)\\Z");

    private static final int PATTERN_GROUP = 2;
    private static final Iec61850ReportNodeHelper NODE_HELPER = new Iec61850ReportNodeHelper(NODES_USING_ID,
            NODE_PATTERN, PATTERN_GROUP);

    private final int systemId;
    private final Iec61850TransformerCommandFactory iec61850TransformerCommandFactory;

    public Iec61850TransformerReportHandler(final int systemId) {
        this.systemId = systemId;
        this.iec61850TransformerCommandFactory = BeanUtil.getBean(Iec61850TransformerCommandFactory.class);
    }

    @Override
    public GetDataSystemIdentifierDto createResult(final List<MeasurementDto> measurements) {
        return new GetDataSystemIdentifierDto(this.systemId, SYSTEM_TYPE, measurements);
    }

    @Override
    public List<MeasurementDto> handleMember(final ReadOnlyNodeContainer member) {
        return NODE_HELPER.getMeasurements(member, this.iec61850TransformerCommandFactory);
    }

}
