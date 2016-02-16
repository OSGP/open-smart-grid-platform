/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class PeriodicMeterReadContainer implements Serializable, OsgpUnitResponse {

    private static final long serialVersionUID = -156966569210717657L;
    private final PeriodType periodType;
    private final OsgpUnit osgpUnit;

    private final List<PeriodicMeterReads> periodicMeterReads;

    public PeriodicMeterReadContainer(final PeriodType periodType, final List<PeriodicMeterReads> periodicMeterReads,
            final OsgpUnit osgpUnit) {
        this.periodicMeterReads = periodicMeterReads;
        this.periodType = periodType;
        this.osgpUnit = osgpUnit;
    }

    public List<PeriodicMeterReads> getPeriodicMeterReads() {
        return this.periodicMeterReads;
    }

    public PeriodType getPeriodType() {
        return this.periodType;
    }

    @Override
    public final OsgpUnit getOsgpUnit() {
        return this.osgpUnit;
    }

}
