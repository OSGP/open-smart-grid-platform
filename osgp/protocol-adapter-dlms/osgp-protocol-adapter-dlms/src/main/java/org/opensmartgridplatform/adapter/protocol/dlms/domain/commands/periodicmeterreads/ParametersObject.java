/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads;

import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodTypeDto;

public class ParametersObject {
    private final PeriodTypeDto periodType;
    private final DateTime beginDateTime;
    private final DateTime endDateTime;
    private final List<DataObject> bufferedObjects;
    private final ChannelDto channel;
    private final boolean isSelectiveAccessSupported;
    private final List<GetResult> results;

    public ParametersObject(final PeriodTypeDto periodType, final DateTime beginDateTime, final DateTime endDateTime,
            final List<DataObject> bufferedObjects, final ChannelDto channel, final boolean isSelectiveAccessSupported,
            final List<GetResult> results) {
        this.periodType = periodType;
        this.beginDateTime = beginDateTime;
        this.endDateTime = endDateTime;
        this.bufferedObjects = bufferedObjects;
        this.channel = channel;
        this.isSelectiveAccessSupported = isSelectiveAccessSupported;
        this.results = results;
    }

    public PeriodTypeDto getPeriodType() {
        return this.periodType;
    }

    public DateTime getBeginDateTime() {
        return this.beginDateTime;
    }

    public DateTime getEndDateTime() {
        return this.endDateTime;
    }

    public List<DataObject> getBufferedObjects() {
        return this.bufferedObjects;
    }

    public ChannelDto getChannel() {
        return this.channel;
    }

    public boolean isSelectiveAccessSupported() {
        return this.isSelectiveAccessSupported;
    }

    public List<GetResult> getResults() {
        return this.results;
    }
}
