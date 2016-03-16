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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import com.alliander.osgp.dto.valueobjects.smartmetering.DlmsMeterValue;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsGas;

public class PeriodicMeterReadsGasConverter
extends
CustomConverter<PeriodicMeterReadsGas, com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas> {

    @Override
    public com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas convert(
            final PeriodicMeterReadsGas source,
            final Type<? extends com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas> destinationType) {
        final AmrProfileStatusCode amrProfileStatusCode = this.mapperFacade.map(source.getAmrProfileStatusCode(),
                AmrProfileStatusCode.class);
        return new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGas(source.getLogTime(),
                this.convert(source.getConsumption()), source.getCaptureTime(), amrProfileStatusCode);
    }

    private OsgpMeterValue convert(final DlmsMeterValue dlmsMeterValue) {
        return this.mapperFacade.map(dlmsMeterValue, OsgpMeterValue.class);
    }

}
