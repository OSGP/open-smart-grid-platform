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

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.EMeterValue;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReads;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadContainer;

public class PeriodicMeterReadsResponseConverter
extends
CustomConverter<PeriodicMeterReadContainer, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsResponse> {

    @Override
    public PeriodicMeterReadsResponse convert(final PeriodicMeterReadContainer source,
            final Type<? extends PeriodicMeterReadsResponse> destinationType) {
        final PeriodicMeterReadsResponse periodicMeterReadsResponse = new PeriodicMeterReadsResponse();
        periodicMeterReadsResponse.setPeriodType(PeriodType.valueOf(source.getPeriodType().name()));
        final List<PeriodicMeterReads> periodicMeterReads = periodicMeterReadsResponse.getPeriodicMeterReads();
        for (final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads m : source
                .getPeriodicMeterReads()) {
            final PeriodicMeterReads meterReads = this.mapperFacade.map(m, PeriodicMeterReads.class);
            periodicMeterReads.add(meterReads);
            // we try to check the unit
            EMeterValue eMeterValue = meterReads.getActiveEnergyImport();
            if (eMeterValue == null) {
                eMeterValue = meterReads.getActiveEnergyImportTariffOne();
            }
            if (eMeterValue != null && !eMeterValue.getUnit().value().equals(source.getOsgpUnit().name())) {
                throw new IllegalStateException(String.format("unit %s in destination differs from unit %s in source",
                        eMeterValue.getUnit(), source.getOsgpUnit()));
            }
        }
        return periodicMeterReadsResponse;
    }

}
