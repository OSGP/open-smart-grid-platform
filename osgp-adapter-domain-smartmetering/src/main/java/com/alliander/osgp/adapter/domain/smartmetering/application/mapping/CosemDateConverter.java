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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDate;

public class CosemDateConverter extends
BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.CosemDate, CosemDate> {

    @Override
    public CosemDate convertTo(final com.alliander.osgp.dto.valueobjects.smartmetering.CosemDate source,
            final Type<CosemDate> destinationType) {
        if (source == null) {
            return null;
        }

        return new CosemDate(source.getYear(), source.getMonth(), source.getDayOfMonth(), source.getDayOfWeek());
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.CosemDate convertFrom(final CosemDate source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.CosemDate> destinationType) {
        if (source == null) {
            return null;
        }

        return new com.alliander.osgp.dto.valueobjects.smartmetering.CosemDate(source.getYear(), source.getMonth(),
                source.getDayOfMonth(), source.getDayOfWeek());
    }
}
