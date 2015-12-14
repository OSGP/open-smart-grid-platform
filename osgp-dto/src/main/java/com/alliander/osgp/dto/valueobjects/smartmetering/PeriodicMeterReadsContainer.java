/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class PeriodicMeterReadsContainer implements Serializable {

    private static final long serialVersionUID = -156966569210717654L;

    private final List<MeterReads> meterReads;
    private final PeriodType periodType;

    public PeriodicMeterReadsContainer(final PeriodType periodType, List<MeterReads> meterReads) {
        this.meterReads = Collections.unmodifiableList(meterReads);
        this.periodType = periodType;
    }

    public List<MeterReads> getMeterReads() {
        return meterReads;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

}