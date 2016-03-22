/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import java.util.List;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequestData;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDayDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;

public class SpecialDaysDataConverter extends CustomConverter<SpecialDaysRequestData, SpecialDaysRequestDataDto> {

    @Override
    public SpecialDaysRequestDataDto convert(final SpecialDaysRequestData source,
            final Type<? extends SpecialDaysRequestDataDto> destinationType) {
        final List<SpecialDayDto> specialDays = this.mapperFacade.mapAsList(source.getSpecialDays(),
                SpecialDayDto.class);
        return new SpecialDaysRequestDataDto(specialDays);
    }

}
