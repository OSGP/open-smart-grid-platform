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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemObisCode;

public class CosemObisCodeConverter extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCode, CosemObisCode> {

    @Override
    public CosemObisCode convertTo(final com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCode source,
            final Type<CosemObisCode> destinationType) {
        if (source == null) {
            return null;
        }

        return new CosemObisCode(source.getA(), source.getB(), source.getC(), source.getD(), source.getE(),
                source.getF());
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCode convertFrom(final CosemObisCode source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCode> destinationType) {
        if (source == null) {
            return null;
        }

        return new com.alliander.osgp.dto.valueobjects.smartmetering.CosemObisCode(source.getA(), source.getB(),
                source.getC(), source.getD(), source.getE(), source.getF());
    }
}
