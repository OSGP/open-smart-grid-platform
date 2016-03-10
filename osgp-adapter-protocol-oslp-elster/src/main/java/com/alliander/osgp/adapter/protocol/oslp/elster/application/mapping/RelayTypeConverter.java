/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.elster.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.RelayType;
import com.alliander.osgp.oslp.Oslp;

public class RelayTypeConverter extends BidirectionalConverter<RelayType, Oslp.RelayType> {

    @Override
    public com.alliander.osgp.oslp.Oslp.RelayType convertTo(final RelayType source,
            final Type<com.alliander.osgp.oslp.Oslp.RelayType> destinationType) {
        if (source == null) {
            return null;
        }

        return Oslp.RelayType.valueOf(source.toString());
    }

    @Override
    public RelayType convertFrom(final com.alliander.osgp.oslp.Oslp.RelayType source,
            final Type<RelayType> destinationType) {
        if (source == null || source == Oslp.RelayType.RT_NOT_SET) {
            return null;
        }

        return RelayType.valueOf(source.toString());
    }

}
