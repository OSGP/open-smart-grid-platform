/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import com.alliander.data.scadameasurementpublishedevent.UnitMultiplier;
import com.alliander.data.scadameasurementpublishedevent.UnitSymbol;

public enum LowVoltageMetaMeasurementType implements LowVoltageMeasurementDefinition {
    FREQUENCY(0, "Frequency", UnitSymbol.Hz),
    TEMPERATURE(1, "Temperature", UnitSymbol.degC),
    IRMS(2, "IRMS-N", UnitSymbol.A);

    private int index;
    private String description;
    private UnitSymbol unitSymbol;
    private UnitMultiplier unitMultiplier;

    LowVoltageMetaMeasurementType(final int index, final String description, final UnitSymbol unitSymbol) {
        this(index, description, unitSymbol, UnitMultiplier.none);
    }

    LowVoltageMetaMeasurementType(final int index, final String description, final UnitSymbol unitSymbol,
            final UnitMultiplier unitMultiplier) {
        this.index = index;
        this.description = description;
        this.unitSymbol = unitSymbol;
        this.unitMultiplier = unitMultiplier;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public UnitSymbol getUnitSymbol() {
        return this.unitSymbol;
    }

    @Override
    public UnitMultiplier getUnitMultiplier() {
        return this.unitMultiplier;
    }
}
