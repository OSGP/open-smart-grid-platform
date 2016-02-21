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
import java.util.Collections;
import java.util.List;

public class PeriodicMeterReadsContainer implements Serializable, ScalerUnitResponse {

    private static final long serialVersionUID = -156966569210717654L;

    private final List<PeriodicMeterReads> meterReads;
    private final PeriodType periodType;
    private final ScalerUnit scalerUnit;

    public PeriodicMeterReadsContainer(final PeriodType periodType, final List<PeriodicMeterReads> meterReads,
            final ScalerUnit scalerUnit) {
        this.meterReads = new ArrayList<PeriodicMeterReads>(meterReads);
        this.periodType = periodType;
        this.scalerUnit = new ScalerUnit(scalerUnit.getDlmsUnit(), scalerUnit.getScaler());
    }

    public List<PeriodicMeterReads> getMeterReads() {
        return Collections.unmodifiableList(this.meterReads);
    }

    public PeriodType getPeriodType() {
        return this.periodType;
    }

    @Override
    public ScalerUnit getScalerUnit() {
        return new ScalerUnit(this.scalerUnit.getDlmsUnit(), this.scalerUnit.getScaler());
    }

}
