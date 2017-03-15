/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

public class SetClockConfigurationRequestDto implements ActionRequestDto {

    private static final long serialVersionUID = -4876678355925159193L;

    protected short timeZoneOffset;

    protected CosemDateTimeDto daylightSavingsBegin;

    protected CosemDateTimeDto daylightSavingsEnd;

    protected byte daylightSavingsDeviation;

    protected boolean daylightSavingsEnabled;

    public SetClockConfigurationRequestDto(final short timeZoneOffset, final CosemDateTimeDto daylightSavingsBegin,
            final CosemDateTimeDto daylightSavingsEnd, final byte daylightSavingsDeviation,
            final boolean daylightSavingsEnabled) {
        this.timeZoneOffset = timeZoneOffset;
        this.daylightSavingsBegin = new CosemDateTimeDto(daylightSavingsBegin);
        this.daylightSavingsEnd = new CosemDateTimeDto(daylightSavingsEnd);
        this.daylightSavingsDeviation = daylightSavingsDeviation;
        this.daylightSavingsEnabled = daylightSavingsEnabled;
    }

    public short getTimeZoneOffset() {
        return this.timeZoneOffset;
    }

    public CosemDateTimeDto getDaylightSavingsBegin() {
        return new CosemDateTimeDto(this.daylightSavingsBegin);
    }

    public CosemDateTimeDto getDaylightSavingsEnd() {
        return new CosemDateTimeDto(this.daylightSavingsEnd);
    }

    public byte getDaylightSavingsDeviation() {
        return this.daylightSavingsDeviation;
    }

    public boolean isDaylightSavingsEnabled() {
        return this.daylightSavingsEnabled;
    }
}
