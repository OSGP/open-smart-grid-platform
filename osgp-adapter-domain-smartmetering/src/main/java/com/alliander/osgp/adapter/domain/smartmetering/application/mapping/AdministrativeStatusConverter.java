/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType;

public class AdministrativeStatusConverter
extends
BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusType, AdministrativeStatusType> {

    @Override
    public AdministrativeStatusType convertTo(
            final com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusType source,
            final Type<AdministrativeStatusType> destinationType) {

        if (source == null) {
            return null;
        }

        return AdministrativeStatusType.fromValue(source.name());
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusType convertFrom(
            final AdministrativeStatusType source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusType> destinationType) {

        if (source == null) {
            return null;
        }

        return com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusType.valueOf(source.name());
    }

}
