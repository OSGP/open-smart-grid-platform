/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.microgrids.valueobjects;

import java.util.Date;

public class Report extends ReportIdentifier {

    private static final long serialVersionUID = 8619381327184505453L;

    protected int sequenceNumber;
    protected Date timeOfEntry;

    public Report(final int sequenceNumber, final Date timeOfEntry, final String id) {
        super(id);
        this.sequenceNumber = sequenceNumber;
        this.timeOfEntry = timeOfEntry;
    }

    public int getSequenceNumber() {
        return this.sequenceNumber;
    }

    public Date getTimeOfEntry() {
        return this.timeOfEntry;
    }

}
