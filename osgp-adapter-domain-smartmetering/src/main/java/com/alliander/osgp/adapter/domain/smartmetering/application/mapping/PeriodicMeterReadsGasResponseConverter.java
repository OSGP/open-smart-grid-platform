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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodType;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGas;

public class PeriodicMeterReadsGasResponseConverter
        extends
        BidirectionalConverter<PeriodicMeterReadsContainerGas, com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas> {

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas convertTo(
            final PeriodicMeterReadsContainerGas source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas> destinationType) {
        final List<PeriodicMeterReadsGas> meterReadsGas = new ArrayList<>(source.getMeterReadsGas().size());
        for (final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas pmr : source
                .getMeterReadsGas()) {

            meterReadsGas.add(this.mapperFacade.convert(pmr, PeriodicMeterReadsGas.class,
                    PeriodicMeterReadsGasConverter.CONVERTER_ID));
        }

        return new com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas(
                PeriodType.valueOf(source.getPeriodType().name()), meterReadsGas);
    }

    @Override
    public PeriodicMeterReadsContainerGas convertFrom(
            final com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas source,
            final Type<PeriodicMeterReadsContainerGas> destinationType) {
        final List<com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas> meterReadsGas = new ArrayList<>(
                source.getMeterReadsGas().size());
        for (final PeriodicMeterReadsGas pmr : source.getMeterReadsGas()) {
            meterReadsGas.add(this.mapperFacade.convert(pmr,
                    com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas.class,
                    PeriodicMeterReadsGasConverter.CONVERTER_ID));
        }

        return new PeriodicMeterReadsContainerGas(
                com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(source.getPeriodType()
                        .name()), meterReadsGas);
    }

}
