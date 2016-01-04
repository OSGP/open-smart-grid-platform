/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType;

public class AdministrativeStatusConverter
        extends
        BidirectionalConverter<AdministrativeStatusType, com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType convertTo(
            final AdministrativeStatusType source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType> destinationType) {

        if (source == null) {
            return null;
        }

        return com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType
                .fromValue(source.name());
    }

    @Override
    public AdministrativeStatusType convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType source,
            final Type<AdministrativeStatusType> destinationType) {

        if (source == null) {
            return null;
        }

        return AdministrativeStatusType.fromValue(source.name());
    }

}
