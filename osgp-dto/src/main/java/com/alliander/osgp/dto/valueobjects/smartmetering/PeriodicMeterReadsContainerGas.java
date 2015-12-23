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

public class PeriodicMeterReadsContainerGas implements Serializable {

    private static final long serialVersionUID = -156966569210717654L;

    private final List<PeriodicMeterReadsGas> meterReadsGas;
    private final PeriodType periodType;

    public PeriodicMeterReadsContainerGas(final PeriodType periodType, final List<PeriodicMeterReadsGas> meterReadsGas) {
        this.meterReadsGas = new ArrayList<PeriodicMeterReadsGas>(meterReadsGas);
        this.periodType = periodType;
    }

    public List<PeriodicMeterReadsGas> getMeterReadsGas() {
        return Collections.unmodifiableList(this.meterReadsGas);
    }

    public PeriodType getPeriodType() {
        return this.periodType;
    }

}
