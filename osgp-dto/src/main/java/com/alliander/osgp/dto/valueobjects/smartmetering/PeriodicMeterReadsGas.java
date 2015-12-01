/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.Date;

public class PeriodicMeterReadsGas extends MeterReadsGas {

    private static final long serialVersionUID = -156966569210717654L;

    private final PeriodType periodType;

    public PeriodicMeterReadsGas(final Date logTime, final PeriodType periodType, final long consumption,
            final Date captureTime) {
        super(logTime, consumption, captureTime);
        this.periodType = periodType;
    }

    public PeriodType getPeriodType() {
        return this.periodType;
    }

}
