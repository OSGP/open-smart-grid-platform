package org.osgp.adapter.protocol.dlms.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.openmuc.jdlms.datatypes.CosemDateFormat;

import com.alliander.osgp.dto.valueobjects.smartmetering.CosemTime;

public class CosemTimeConverter extends BidirectionalConverter<CosemTime, org.openmuc.jdlms.datatypes.CosemTime> {

    @Override
    public org.openmuc.jdlms.datatypes.CosemTime convertTo(final CosemTime source,
            final Type<org.openmuc.jdlms.datatypes.CosemTime> destinationType) {

        return new org.openmuc.jdlms.datatypes.CosemTime(source.getHour(), source.getMinute(), source.getSecond(),
                source.getHundredths());
    }

    @Override
    public CosemTime convertFrom(final org.openmuc.jdlms.datatypes.CosemTime source,
            final Type<CosemTime> destinationType) {
        if (source == null) {
            return null;
        }

        final int hour = source.valueFor(CosemDateFormat.Field.HOUR);
        final int minute = source.valueFor(CosemDateFormat.Field.MINUTE);
        final int second = source.valueFor(CosemDateFormat.Field.SECOND);
        final int hundredths = source.valueFor(CosemDateFormat.Field.HUNDREDTHS);
        return new com.alliander.osgp.dto.valueobjects.smartmetering.CosemTime(hour, minute, second, hundredths);
    }

}
