/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.util.List;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGas;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;

public class PeriodicMeterReadsResponseGasConverter
extends
CustomConverter<PeriodicMeterReadsContainerGas, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasResponse> {

    @Override
    public PeriodicMeterReadsGasResponse convert(final PeriodicMeterReadsContainerGas source,
            final Type<? extends PeriodicMeterReadsGasResponse> destinationType) {
        final PeriodicMeterReadsGasResponse periodicMeterReadsResponse = new PeriodicMeterReadsGasResponse();
        periodicMeterReadsResponse.setPeriodType(PeriodType.valueOf(source.getPeriodType().name()));
        final List<PeriodicMeterReadsGas> periodicMeterReads = periodicMeterReadsResponse.getPeriodicMeterReadsGas();
        for (final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas m : source
                .getMeterReadsGas()) {
            final PeriodicMeterReadsGas meterReads = this.mapperFacade.map(m, PeriodicMeterReadsGas.class);
            periodicMeterReads.add(meterReads);
            if (meterReads.getConsumption() != null
                    && !meterReads.getConsumption().getUnit().value().equals(source.getOsgpUnit().name())) {
                throw new IllegalStateException(String.format("unit %s in destination differs from unit %s in source",
                        meterReads.getConsumption().getUnit(), source.getOsgpUnit()));
            }
        }
        return periodicMeterReadsResponse;
    }
}
