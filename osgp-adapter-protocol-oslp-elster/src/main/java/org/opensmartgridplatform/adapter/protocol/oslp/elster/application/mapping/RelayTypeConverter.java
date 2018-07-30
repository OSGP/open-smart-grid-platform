/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.mapping;

import org.opensmartgridplatform.dto.valueobjects.RelayTypeDto;
import org.opensmartgridplatform.oslp.Oslp;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class RelayTypeConverter extends BidirectionalConverter<RelayTypeDto, Oslp.RelayType> {

    @Override
    public org.opensmartgridplatform.oslp.Oslp.RelayType convertTo(final RelayTypeDto source,
            final Type<org.opensmartgridplatform.oslp.Oslp.RelayType> destinationType, final MappingContext context) {
        if (source == null) {
            return null;
        }

        return Oslp.RelayType.valueOf(source.toString());
    }

    @Override
    public RelayTypeDto convertFrom(final org.opensmartgridplatform.oslp.Oslp.RelayType source,
            final Type<RelayTypeDto> destinationType, final MappingContext context) {
        if ((source == null) || (source == Oslp.RelayType.RT_NOT_SET)) {
            return null;
        }

        return RelayTypeDto.valueOf(source.toString());
    }

    @Override
    public boolean canConvert(final Type<?> sourceType, final Type<?> destinationType) {
        return this.sourceType.isAssignableFrom(sourceType) && this.destinationType.equals(destinationType);
    }

}
