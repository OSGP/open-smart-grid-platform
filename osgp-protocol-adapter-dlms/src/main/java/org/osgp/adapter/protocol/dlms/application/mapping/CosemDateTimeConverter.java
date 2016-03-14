/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.mapping;

import java.util.List;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.openmuc.jdlms.datatypes.CosemDateFormat;
import org.openmuc.jdlms.datatypes.CosemDateTime.ClockStatus;

import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDate;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime;
import com.alliander.osgp.dto.valueobjects.smartmetering.CosemTime;

public class CosemDateTimeConverter extends
        BidirectionalConverter<CosemDateTime, org.openmuc.jdlms.datatypes.CosemDateTime> {

    @Override
    public org.openmuc.jdlms.datatypes.CosemDateTime convertTo(final CosemDateTime source,
            final Type<org.openmuc.jdlms.datatypes.CosemDateTime> destinationType) {

        final CosemTime time = source.getTime();
        final CosemDate date = source.getDate();
        final List<ClockStatus> clockStatus = ClockStatus.clockStatusFrom((byte) source.getClockStatus().getStatus());

        return new org.openmuc.jdlms.datatypes.CosemDateTime(date.getYear(), date.getMonth(), date.getDayOfMonth(),
                date.getDayOfWeek(), time.getHour(), time.getMinute(), time.getSecond(), time.getHundredths(),
                source.getDeviation(), clockStatus.toArray(new ClockStatus[clockStatus.size()]));
    }

    @Override
    public CosemDateTime convertFrom(final org.openmuc.jdlms.datatypes.CosemDateTime source,
            final Type<CosemDateTime> destinationType) {
        if (source == null) {
            return null;
        }

        final int year = source.valueFor(CosemDateFormat.Field.YEAR);
        final int month = source.valueFor(CosemDateFormat.Field.MONTH);
        final int dayOfMonth = source.valueFor(CosemDateFormat.Field.DAY_OF_MONTH);
        final int dayOfWeek = source.valueFor(CosemDateFormat.Field.DAY_OF_WEEK);
        final com.alliander.osgp.dto.valueobjects.smartmetering.CosemDate date = new com.alliander.osgp.dto.valueobjects.smartmetering.CosemDate(
                year, month, dayOfMonth, dayOfWeek);

        final int hour = source.valueFor(CosemDateFormat.Field.HOUR);
        final int minute = source.valueFor(CosemDateFormat.Field.MINUTE);
        final int second = source.valueFor(CosemDateFormat.Field.SECOND);
        final int hundredths = source.valueFor(CosemDateFormat.Field.HUNDREDTHS);
        final com.alliander.osgp.dto.valueobjects.smartmetering.CosemTime time = new com.alliander.osgp.dto.valueobjects.smartmetering.CosemTime(
                hour, minute, second, hundredths);

        final int deviation = source.valueFor(CosemDateFormat.Field.DEVIATION);

        final int clockStatusValue = source.valueFor(CosemDateFormat.Field.CLOCK_STATUS);
        final com.alliander.osgp.dto.valueobjects.smartmetering.ClockStatus clockStatus = new com.alliander.osgp.dto.valueobjects.smartmetering.ClockStatus(
                clockStatusValue);

        return new com.alliander.osgp.dto.valueobjects.smartmetering.CosemDateTime(date, time, deviation, clockStatus);
    }

}
