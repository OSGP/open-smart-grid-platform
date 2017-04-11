/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.microgrids;

import java.util.Date;

import org.joda.time.DateTime;

public class EmptyReportDto extends ReportDto {

    private static final long serialVersionUID = -9091954947977865799L;

    private static int SEQUENCE_NUMBER = -1;
    private static Date TIME_OF_ENTRY = new DateTime("2000-01-01T00:00:00.000-00:00").toDate();
    private static String IDENTIFIER = "PV1_Measurements";

    public EmptyReportDto() {
        this(SEQUENCE_NUMBER, TIME_OF_ENTRY, IDENTIFIER);
    }

    private EmptyReportDto(final int sequenceNumber, final Date timeOfEntry, final String identifier) {
        super(sequenceNumber, timeOfEntry, identifier);
    }
}
