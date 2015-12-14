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
        final List<PeriodicMeterReadsGas> r = new ArrayList<>(source.getPeriodicMeterReadsGas().size());
        for (final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas pmr : source
                .getPeriodicMeterReadsGas()) {
            r.add(new PeriodicMeterReadsGas(pmr.getLogTime(), PeriodType.valueOf(pmr.getPeriodType().name()), pmr
                    .getConsumption(), pmr.getCaptureTime()));
        }

        return new com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas(r);
    }

    @Override
    public PeriodicMeterReadsContainerGas convertFrom(
            final com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas source,
            final Type<PeriodicMeterReadsContainerGas> destinationType) {
        final List<com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas> r = new ArrayList<>(
                source.getPeriodicMeterReadsGas().size());
        for (final PeriodicMeterReadsGas pmr : source.getPeriodicMeterReadsGas()) {
            r.add(new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas(pmr.getLogTime(),
                    com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(pmr.getPeriodType()
                            .name()), pmr.getConsumption(), pmr.getCaptureTime()));
        }

        return new PeriodicMeterReadsContainerGas(r);
    }

}
