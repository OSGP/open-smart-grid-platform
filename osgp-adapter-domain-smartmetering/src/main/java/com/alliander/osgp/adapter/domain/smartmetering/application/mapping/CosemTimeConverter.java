/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemTime;

public class CosemTimeConverter extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.CosemTime, CosemTime> {

    @Override
    public CosemTime convertTo(final com.alliander.osgp.dto.valueobjects.smartmetering.CosemTime source,
            final Type<CosemTime> destinationType) {
        if (source == null) {
            return null;
        }

        return new CosemTime(source.getHour(), source.getMinute(), source.getSecond(), source.getHundredths());
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.CosemTime convertFrom(final CosemTime source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.CosemTime> destinationType) {
        if (source == null) {
            return null;
        }

        return new com.alliander.osgp.dto.valueobjects.smartmetering.CosemTime(source.getHour(), source.getMinute(),
                source.getSecond(), source.getHundredths());
    }
}
