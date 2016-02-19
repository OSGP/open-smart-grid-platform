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

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.EMeterValue;

public class EMeterValueConverter extends CustomConverter<Double, EMeterValue> {

    @Override
    public EMeterValue convert(final Double source, final Type<? extends EMeterValue> destinationType) {
        final EMeterValue eMeterValue = new EMeterValue();
        eMeterValue.setValue(BigDecimal.valueOf(source));
        eMeterValue.setUnit(eMeterValue.getUnit());
        return eMeterValue;
    }

}
