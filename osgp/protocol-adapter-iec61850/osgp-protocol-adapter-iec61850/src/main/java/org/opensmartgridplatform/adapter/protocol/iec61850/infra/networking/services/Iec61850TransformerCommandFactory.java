/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.RtuReadCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import org.opensmartgridplatform.dto.valueobjects.microgrids.MeasurementDto;
import org.springframework.stereotype.Component;

@Component
public class Iec61850TransformerCommandFactory extends AbstractIec61850RtuReadCommandFactory {

    public Iec61850TransformerCommandFactory() {
        super(rtuCommandMap(), dataAttributesUsingFilterId());
    }

    private static final Set<DataAttribute> dataAttributesUsingFilterId() {
        return EnumSet.noneOf(DataAttribute.class);
    }

    private static Map<String, RtuReadCommand<MeasurementDto>> rtuCommandMap() {

        final CommandsByAttributeBuilder builder = new CommandsByAttributeBuilder();

        final Set<DataAttribute> simpleCommandAttributes = EnumSet.of(DataAttribute.TEMPERATURE,
                DataAttribute.ACTUAL_POWER);
        builder.withSimpleCommandsFor(simpleCommandAttributes);

        return builder.build();
    }

}
