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

import org.springframework.stereotype.Component;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;

@Component
public class PeriodicMeterReadsGasResponseConverter
extends
CustomConverter<com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas, PeriodicMeterReadsContainerGas> {

    @Override
    public PeriodicMeterReadsContainerGas convert(
            final com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas source,
            final Type<? extends PeriodicMeterReadsContainerGas> destinationType) {
        return new PeriodicMeterReadsContainerGas(
                com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(source.getPeriodType()
                        .name()), this.mapperFacade.mapAsList(source.getMeterReadsGas(),
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas.class));
    }

}
