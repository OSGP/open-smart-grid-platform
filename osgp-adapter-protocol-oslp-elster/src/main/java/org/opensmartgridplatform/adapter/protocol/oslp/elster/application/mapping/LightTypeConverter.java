/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.mapping;

import org.opensmartgridplatform.dto.valueobjects.LightTypeDto;
import org.opensmartgridplatform.oslp.Oslp;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class LightTypeConverter extends BidirectionalConverter<LightTypeDto, Oslp.LightType> {

    @Override
    public org.opensmartgridplatform.oslp.Oslp.LightType convertTo(final LightTypeDto source,
            final Type<org.opensmartgridplatform.oslp.Oslp.LightType> destinationType, final MappingContext context) {
        if (source == null) {
            return null;
        }

        return Oslp.LightType.valueOf(source.toString());
    }

    @Override
    public LightTypeDto convertFrom(final org.opensmartgridplatform.oslp.Oslp.LightType source,
            final Type<LightTypeDto> destinationType, final MappingContext context) {
        if (source == null || source == Oslp.LightType.LT_NOT_SET) {
            return null;
        }

        return LightTypeDto.valueOf(source.toString());
    }

}
