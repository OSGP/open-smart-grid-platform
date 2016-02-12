/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.util.ArrayList;
import java.util.List;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGas;

public class PeriodicMeterReadsGasResponseConverter
extends
BidirectionalConverter<PeriodicMeterReadsContainerGas, com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas> {
    private final StandardUnitCalculator standardUnitCalculator;

    public PeriodicMeterReadsGasResponseConverter(final StandardUnitCalculator standardUnitCalculator) {
        super();
        this.standardUnitCalculator = standardUnitCalculator;
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas convertTo(
            final PeriodicMeterReadsContainerGas source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas> destinationType) {
        throw new IllegalStateException(
                "mapping a response meant for the platform layer to a response from the protocol layer should not be necessary");
    }

    @Override
    public PeriodicMeterReadsContainerGas convertFrom(
            final com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas source,
            final Type<PeriodicMeterReadsContainerGas> destinationType) {
        final List<com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas> meterReadsGas = new ArrayList<>(
                source.getMeterReadsGas().size());
        for (final PeriodicMeterReadsGas pmr : source.getMeterReadsGas()) {

            final AmrProfileStatusCode amrProfileStatusCode = this.mapperFacade.map(pmr.getAmrProfileStatusCode(),
                    AmrProfileStatusCode.class);
            meterReadsGas.add(new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas(pmr
                    .getLogTime(), this.standardUnitCalculator.calculateStandardizedValue(pmr.getConsumption(),
                            source.getScalerUnit()), pmr.getCaptureTime(), amrProfileStatusCode));
        }

        return new PeriodicMeterReadsContainerGas(
                com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(source.getPeriodType()
                        .name()), meterReadsGas);
    }

}
