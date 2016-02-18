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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;

@Component
public class PeriodicMeterReadsRequestConverter
        extends
        CustomConverter<PeriodicMeterReadsQuery, com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQuery> {

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQuery convert(
            final PeriodicMeterReadsQuery source,
            final Type<? extends com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQuery> destinationType) {
        return new com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQuery(
                com.alliander.osgp.dto.valueobjects.smartmetering.PeriodType.valueOf(source.getPeriodType().name()),
                source.getBeginDate(), source.getEndDate());
    }

}
