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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsGas;

@Component
public class ActualMeterReadsGasConverter extends
        CustomConverter<com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsGas, ActualMeterReadsGas> {
    @Autowired
    private StandardUnitConverter standardUnitConverter;

    @Override
    public ActualMeterReadsGas convert(
            final com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsGas source,
            final Type<? extends ActualMeterReadsGas> destinationType) {
        return new ActualMeterReadsGas(source.getLogTime(), this.standardUnitConverter.calculateStandardizedValue(
                source.getConsumption(), source), source.getCaptureTime(),
                this.standardUnitConverter.toStandardUnit(source));
    }
}
