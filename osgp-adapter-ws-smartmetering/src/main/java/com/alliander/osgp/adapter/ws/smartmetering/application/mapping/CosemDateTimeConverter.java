package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ClockStatus;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDate;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemDateTime;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CosemTime;

public class CosemDateTimeConverter extends CustomConverter<byte[], CosemDateTime> {

    @Override
    public CosemDateTime convert(final byte[] source, final Type<? extends CosemDateTime> destinationType) {
        final int year = (source[0] << 8) | (source[1] & 0xff);
        final int month = source[2] & 0xFF;
        final int dayOfMonth = source[3] & 0xFF;
        final int dayOfWeek = source[4] & 0xFF;
        final int hour = source[5] & 0xFF;
        final int minute = source[6] & 0xFF;
        final int second = source[7] & 0xFF;
        final int hundredths = source[8] & 0xFF;
        final int deviation = (source[9] << 8) | (source[10] & 0xff);

        final ClockStatus clockStatus = new ClockStatus(source[11]);
        final CosemTime time = new CosemTime(hour, minute, second, hundredths);
        final CosemDate date = new CosemDate(year, month, dayOfMonth, dayOfWeek);

        return new CosemDateTime(date, time, deviation, clockStatus);
    }
}
