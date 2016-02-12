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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsGas;

public class ActualMeterReadsGasConverter
extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsGas, ActualMeterReadsGas> {
    private final StandardUnitCalculator standardUnitCalculator;

    public ActualMeterReadsGasConverter(final StandardUnitCalculator standardUnitCalculator) {
        super();
        this.standardUnitCalculator = standardUnitCalculator;
    }

    @Override
    public ActualMeterReadsGas convertTo(
            final com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsGas source,
            final Type<ActualMeterReadsGas> destinationType) {

        return new ActualMeterReadsGas(source.getLogTime(), this.standardUnitCalculator.calculateStandardizedValue(
                source.getConsumption(), source.getScalerUnit()), source.getCaptureTime());
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsGas convertFrom(
            final ActualMeterReadsGas source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsGas> destinationType) {

        throw new IllegalStateException(
                "mapping a response meant for the platform layer to a response from the protocol layer should not be necessary");
    }
}
