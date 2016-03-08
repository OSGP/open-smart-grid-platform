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
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDay;

public class SpecialDaysDataConverter
        extends
        CustomConverter<SpecialDaysRequestData, com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestData> {

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestData convert(
            final SpecialDaysRequestData source,
            final Type<? extends com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestData> destinationType) {
        final List<SpecialDay> specialDays = this.mapperFacade.mapAsList(source.getSpecialDays(), SpecialDay.class);
        return new com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestData(specialDays);
    }

}
