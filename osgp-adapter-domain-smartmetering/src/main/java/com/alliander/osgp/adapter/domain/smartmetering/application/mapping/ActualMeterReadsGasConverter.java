/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReadsGas;

public class ActualMeterReadsGasConverter extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsGas, MeterReadsGas> {

    @Override
    public MeterReadsGas convertTo(final com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsGas source,
            final Type<MeterReadsGas> destinationType) {

        return new MeterReadsGas(source.getLogTime(), source.getConsumption(), source.getCaptureTime());
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsGas convertFrom(final MeterReadsGas source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsGas> destinationType) {

        return new com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsGas(source.getLogTime(),
                source.getConsumption(), source.getCaptureTime());
    }
}
