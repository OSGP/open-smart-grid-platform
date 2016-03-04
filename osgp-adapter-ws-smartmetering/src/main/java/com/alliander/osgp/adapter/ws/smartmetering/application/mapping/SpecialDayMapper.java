package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.nio.ByteBuffer;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDate;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDay;

public class SpecialDayMapper extends
        CustomConverter<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDay, SpecialDay> {

    @Override
    public SpecialDay convert(final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDay source,
            final Type<? extends SpecialDay> destinationType) {
        final ByteBuffer bb = ByteBuffer.wrap(source.getSpecialDayDate());
        final SpecialDay specialDay = new SpecialDay(new CosemDate(bb.getInt(), bb.get(), bb.get()), source.getDayId());

        return specialDay;
    }

}
