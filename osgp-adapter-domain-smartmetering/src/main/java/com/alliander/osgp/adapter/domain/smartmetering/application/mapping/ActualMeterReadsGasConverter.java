/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReadsGas;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsMeterValue;

public class ActualMeterReadsGasConverter extends
        CustomConverter<com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsGas, MeterReadsGas> {

    @Override
    public MeterReadsGas convert(final com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsGas source,
            final Type<? extends MeterReadsGas> destinationType) {
        return new MeterReadsGas(source.getLogTime(), this.convert(source.getConsumption()), source.getCaptureTime());
    }

    private OsgpMeterValue convert(final DlmsMeterValue dlmsMeterValue) {
        return this.mapperFacade.map(dlmsMeterValue, OsgpMeterValue.class);
    }
}
