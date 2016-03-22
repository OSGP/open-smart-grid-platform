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
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;

public class AdministrativeStatusConverter extends
        BidirectionalConverter<AdministrativeStatusTypeDto, AdministrativeStatusType> {

    @Override
    public AdministrativeStatusType convertTo(final AdministrativeStatusTypeDto source,
            final Type<AdministrativeStatusType> destinationType) {

        if (source == null) {
            return null;
        }

        return AdministrativeStatusType.fromValue(source.name());
    }

    @Override
    public AdministrativeStatusTypeDto convertFrom(final AdministrativeStatusType source,
            final Type<AdministrativeStatusTypeDto> destinationType) {

        if (source == null) {
            return null;
        }

        return AdministrativeStatusTypeDto.valueOf(source.name());
    }

}
