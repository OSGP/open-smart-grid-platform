/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.microgrids;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class EmptyReportDto extends ReportDto {

    private static final long serialVersionUID = -9091954947977865799L;

    private static final int SEQUENCE_NUMBER = -1;
    private static final DateTime TIME_OF_ENTRY = new DateTime(2000, 1, 1, 0, 0, DateTimeZone.UTC);
    private static final String ID = "no report";

    public EmptyReportDto() {
        this(SEQUENCE_NUMBER, TIME_OF_ENTRY, ID);
    }

    private EmptyReportDto(final int sequenceNumber, final DateTime timeOfEntry, final String identifier) {
        super(sequenceNumber, timeOfEntry, identifier);
    }
}
