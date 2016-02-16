/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class PeriodicMeterReadsContainerGas implements Serializable, OsgpUnitResponse {

    private static final long serialVersionUID = -156966569210717654L;

    private final List<PeriodicMeterReadsGas> meterReadsGas;
    private final PeriodType periodType;
    private final OsgpUnit osgpUnit;

    public PeriodicMeterReadsContainerGas(final PeriodType periodType, final List<PeriodicMeterReadsGas> meterReadsGas,
            final OsgpUnit osgpUnit) {
        this.meterReadsGas = Collections.unmodifiableList(meterReadsGas);
        this.periodType = periodType;
        this.osgpUnit = osgpUnit;
    }

    public List<PeriodicMeterReadsGas> getMeterReadsGas() {
        return this.meterReadsGas;
    }

    public PeriodType getPeriodType() {
        return this.periodType;
    }

    @Override
    public final OsgpUnit getOsgpUnit() {
        return this.osgpUnit;
    }

}
