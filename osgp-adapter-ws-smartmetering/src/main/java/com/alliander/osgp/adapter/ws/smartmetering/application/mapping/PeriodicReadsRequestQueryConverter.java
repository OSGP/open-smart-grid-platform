/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestQuery;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;

public class PeriodicReadsRequestQueryConverter
extends
CustomConverter<com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestQuery, PeriodicMeterReadsQuery> {

    @Override
    public PeriodicMeterReadsQuery convert(final PeriodicReadsRequestQuery source,
            final Type<? extends PeriodicMeterReadsQuery> destinationType) {
        return new PeriodicMeterReadsQuery(
                com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(source
                        .getPeriodicReadsRequestData().getPeriodType().name()), source.getPeriodicReadsRequestData()
                        .getBeginDate().toGregorianCalendar().getTime(), source.getPeriodicReadsRequestData()
                        .getEndDate().toGregorianCalendar().getTime(), false, "");
    }

}
