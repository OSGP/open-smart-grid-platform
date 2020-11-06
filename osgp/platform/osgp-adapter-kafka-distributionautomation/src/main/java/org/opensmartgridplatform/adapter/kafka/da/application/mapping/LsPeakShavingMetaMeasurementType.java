/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.alliander.data.scadameasurementpublishedevent.UnitMultiplier;
import com.alliander.data.scadameasurementpublishedevent.UnitSymbol;

public enum LsPeakShavingMetaMeasurementType {
    FREQUENCY(0, "Frequency", UnitSymbol.Hz),
    TEMPERATURE(1, "Temperature", UnitSymbol.degC),
    IMRS(2, "IMRS-N", UnitSymbol.A);

    private static final Map<Integer, LsPeakShavingMetaMeasurementType> MEASUREMENTS_MAP = Arrays
            .stream(LsPeakShavingMetaMeasurementType.values())
            .collect(Collectors.toMap(LsPeakShavingMetaMeasurementType::getIndex, e -> e));

    private int index;
    private String description;
    private UnitSymbol unitSymbol;
    private UnitMultiplier unitMultiplier;

    LsPeakShavingMetaMeasurementType(final int index, final String description, final UnitSymbol unitSymbol) {
        this(index, description, unitSymbol, UnitMultiplier.none);
    }

    LsPeakShavingMetaMeasurementType(final int index, final String description, final UnitSymbol unitSymbol,
            final UnitMultiplier unitMultiplier) {
        this.index = index;
        this.description = description;
        this.unitSymbol = unitSymbol;
        this.unitMultiplier = unitMultiplier;
    }

    public static LsPeakShavingMetaMeasurementType getMeasurementType(final int index) {
        return MEASUREMENTS_MAP.get(index);
    }

    public static int getNumberOfElements() {
        return MEASUREMENTS_MAP.size();
    }

    public int getIndex() {
        return this.index;
    }

    public String getDescription() {
        return this.description;
    }

    public UnitSymbol getUnitSymbol() {
        return this.unitSymbol;
    }

    public UnitMultiplier getUnitMultiplier() {
        return this.unitMultiplier;
    }
}
