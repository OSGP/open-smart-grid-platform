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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGas;

@Component
public class PeriodicMeterReadsGasConverter
extends
CustomConverter<PeriodicMeterReadsGas, com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas> {
    @Autowired
    private StandardUnitConverter standardUnitConverter;

    @Override
    public com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas convert(
            final PeriodicMeterReadsGas source,
            final Type<? extends com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas> destinationType) {
        final AmrProfileStatusCode amrProfileStatusCode = this.mapperFacade.map(source.getAmrProfileStatusCode(),
                AmrProfileStatusCode.class);
        return new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas(source.getLogTime(),
                this.standardUnitConverter.calculateStandardizedValue(source.getConsumption()),
                source.getCaptureTime(), amrProfileStatusCode);
    }

}
