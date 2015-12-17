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

public class PeriodicMeterReadsContainerGas implements Serializable {

    private static final long serialVersionUID = -156966569210717654L;

    private final List<MeterReadsGas> MeterReadsGas;
    private final PeriodType periodType;

    public PeriodicMeterReadsContainerGas(final PeriodType periodType, final List<MeterReadsGas> MeterReadsGas) {
        this.MeterReadsGas = Collections.unmodifiableList(MeterReadsGas);
        this.periodType = periodType;
    }

    public List<MeterReadsGas> getMeterReadsGas() {
        return this.MeterReadsGas;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

}