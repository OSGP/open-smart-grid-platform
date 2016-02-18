/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.math.BigDecimal;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.GMeterValue;

public class GMeterValueConverter extends CustomConverter<Double, GMeterValue> {

    @Override
    public GMeterValue convert(final Double source, final Type<? extends GMeterValue> destinationType) {
        final GMeterValue gMeterValue = new GMeterValue();
        gMeterValue.setValue(BigDecimal.valueOf(source));
        gMeterValue.setUnit(gMeterValue.getUnit());
        return gMeterValue;
    }

}
