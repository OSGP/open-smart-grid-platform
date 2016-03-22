/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDay;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDayDto;

public class SpecialDayConverter extends CustomConverter<SpecialDay, SpecialDayDto> {

    @Override
    public SpecialDayDto convert(final SpecialDay source, final Type<? extends SpecialDayDto> destinationType) {
        return new SpecialDayDto(this.mapperFacade.map(source.getSpecialDayDate(), CosemDateDto.class),
                source.getDayId());
    }

}
