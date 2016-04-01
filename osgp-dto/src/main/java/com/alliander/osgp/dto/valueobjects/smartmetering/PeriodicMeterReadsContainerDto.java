/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PeriodicMeterReadsContainerDto implements Serializable {

    private static final long serialVersionUID = -156966569210717654L;

    private final List<PeriodicMeterReadsDto> periodicMeterReads;
    private final PeriodTypeDto periodType;

    public PeriodicMeterReadsContainerDto(final PeriodTypeDto periodType,
            final List<PeriodicMeterReadsDto> periodicMeterReads) {
        this.periodicMeterReads = new ArrayList<PeriodicMeterReadsDto>(periodicMeterReads);
        this.periodType = periodType;
    }

    public List<PeriodicMeterReadsDto> getPeriodicMeterReads() {
        return new ArrayList<PeriodicMeterReadsDto>(this.periodicMeterReads);
    }

    public PeriodTypeDto getPeriodType() {
        return this.periodType;
    }

}
