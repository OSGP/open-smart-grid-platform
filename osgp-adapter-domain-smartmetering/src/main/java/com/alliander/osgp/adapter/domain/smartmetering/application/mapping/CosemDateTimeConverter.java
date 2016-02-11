package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.smartmetering.ClockStatus;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDate;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemTime;

public class CosemDateTimeConverter extends BidirectionalConverter<CosemDateTime, byte[]> {

    @Override
    public byte[] convertTo(final CosemDateTime source, final Type<byte[]> destinationType) {
        throw new UnsupportedOperationException("Conversion from CosemDateTime to byte array is not yet supported");
    }

    @Override
    public CosemDateTime convertFrom(final byte[] source, final Type<CosemDateTime> destinationType) {
        final int year = (source[0] << 8) + (source[1] & 0xff);
        final int month = source[2];
        final int dayOfMonth = source[3];
        final int dayOfWeek = source[4];
        final int hour = source[5];
        final int minute = source[6];
        final int second = source[7];
        final int hundredths = source[8];
        final int deviation = (source[9] << 8) + (source[10] & 0xff);
        final ClockStatus clockStatus = new ClockStatus(source[11]);

        final CosemTime time = new CosemTime(hour, minute, second, hundredths);
        final CosemDate date = new CosemDate(year, month, dayOfMonth, dayOfWeek);

        return new CosemDateTime(date, time, deviation, clockStatus);
    }
}
