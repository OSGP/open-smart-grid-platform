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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas;

public class PeriodicMeterReadsGasConverter
extends
BidirectionalConverter<PeriodicMeterReadsGas, com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGas> {

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGas convertTo(
            final PeriodicMeterReadsGas source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGas> destinationType) {

        final com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCode amrProfileStatusCode = this.mapperFacade
                .map(source.getAmrProfileStatusCode(),
                        com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCode.class);

        return new com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGas(source.getLogTime(),
                source.getConsumption(), source.getCaptureTime(), amrProfileStatusCode);
    }

    @Override
    public PeriodicMeterReadsGas convertFrom(
            final com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGas source,
            final Type<PeriodicMeterReadsGas> destinationType) {

        final AmrProfileStatusCode amrProfileStatusCode = this.mapperFacade.map(source.getAmrProfileStatusCode(),
                AmrProfileStatusCode.class);

        return new PeriodicMeterReadsGas(source.getLogTime(), source.getConsumption(), source.getCaptureTime(),
                amrProfileStatusCode);
    }
}
