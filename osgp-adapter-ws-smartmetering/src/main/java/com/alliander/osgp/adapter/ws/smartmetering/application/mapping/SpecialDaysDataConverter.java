package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.util.List;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDaysRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDay;

public class SpecialDaysDataConverter
        extends
        CustomConverter<SpecialDaysRequestData, com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequestData> {

    @Override
    public com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequestData convert(
            final SpecialDaysRequestData source,
            final Type<? extends com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequestData> destinationType) {
        final List<SpecialDay> specialDays = this.mapperFacade.mapAsList(source.getSpecialDays(), SpecialDay.class);
        return new com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequestData(specialDays);
    }

}
