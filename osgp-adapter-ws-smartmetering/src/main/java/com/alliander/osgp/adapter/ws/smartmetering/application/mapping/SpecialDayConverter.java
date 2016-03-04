package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.nio.ByteBuffer;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDate;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDay;

public class SpecialDayConverter extends
CustomConverter<com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDay, SpecialDay> {

    @Override
    public SpecialDay convert(final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDay source,
            final Type<? extends SpecialDay> destinationType) {
        final ByteBuffer bb = ByteBuffer.wrap(source.getSpecialDayDate());
        final short year = bb.getShort();
        final byte month = bb.get();
        final byte day = bb.get();
        final SpecialDay specialDay = new SpecialDay(new CosemDate(year == -1 ? 0xFFFF : year, month, day),
                source.getDayId());

        return specialDay;
    }

}
