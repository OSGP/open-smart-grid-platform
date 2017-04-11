/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.application.mapping;

import java.util.Date;

import org.joda.time.DateTime;
import org.openmuc.openiec61850.Report;

import com.alliander.osgp.dto.valueobjects.microgrids.ReportDto;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

public class ReportConverter extends CustomConverter<Report, ReportDto> {

    /**
     * The EntryTime from IEC61850 has timestamp values relative to 01-01-1984.
     * TimeStamp values and Java date time values have milliseconds since
     * 01-01-1970. The milliseconds between these representations are in the
     * following offset.
     */
    private static final long IEC61850_ENTRY_TIME_OFFSET = 441763200000L;

    @Override
    public ReportDto convert(final Report source, final Type<? extends ReportDto> destinationType) {
        final Date date = new DateTime(source.getTimeOfEntry().getTimestampValue() + IEC61850_ENTRY_TIME_OFFSET).toDate();
        return new ReportDto(source.getSqNum(), date, source.getRptId());
    }
}
