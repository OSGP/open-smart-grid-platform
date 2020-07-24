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

import org.opensmartgridplatform.adapter.kafka.da.avro.UnitMultiplier;
import org.opensmartgridplatform.adapter.kafka.da.avro.UnitSymbol;

public enum LsPeakShavingMeasurementType {
    VOLTAGE_L1(1, "U-L1-E", UnitSymbol.V),
    VOLTAGE_L2(2, "U-L2-E", UnitSymbol.V),
    VOLTAGE_L3(3, "U-L3-E", UnitSymbol.V),
    CURRENT_L1(4, "I-L1", UnitSymbol.A),
    CURRENT_L2(5, "I-L2", UnitSymbol.A),
    CURRENT_L3(6, "I-L3", UnitSymbol.A),
    TOTAL_ACTIVE_POWER(7, "SomP", UnitSymbol.W, UnitMultiplier.k),
    TOTAL_REACTIVE_POWER(8, "SomQ", UnitSymbol.VAr, UnitMultiplier.k),
    ACTIVE_POWER_L1(9, "P-L1", UnitSymbol.W, UnitMultiplier.k),
    ACTIVE_POWER_L2(10, "P-L2", UnitSymbol.W, UnitMultiplier.k),
    ACTIVE_POWER_L3(11, "P-L3", UnitSymbol.W, UnitMultiplier.k),
    REACTIVE_POWER_L1(12, "Q-L1", UnitSymbol.VAr, UnitMultiplier.k),
    REACTIVE_POWER_L2(13, "Q-L2", UnitSymbol.VAr, UnitMultiplier.k),
    REACTIVE_POWER_L3(14, "Q-L3", UnitSymbol.VAr, UnitMultiplier.k),
    POWER_FACTOR_L1(15, "PF-L1", UnitSymbol.none),
    POWER_FACTOR_L2(16, "PF-L2", UnitSymbol.none),
    POWER_FACTOR_L3(17, "PF-L3", UnitSymbol.none),
    CURRENT_TOTAL_HARMONIC_DISTORTION_L1(18, "THDi-L1", UnitSymbol.PerCent),
    CURRENT_TOTAL_HARMONIC_DISTORTION_L2(19, "THDi-L2", UnitSymbol.PerCent),
    CURRENT_TOTAL_HARMONIC_DISTORTION_L3(20, "THDi-L3", UnitSymbol.PerCent),
    CURRENT_HARMONIC_L1_H3(21, "I1-H3", UnitSymbol.A),
    CURRENT_HARMONIC_L2_H3(22, "I2-H3", UnitSymbol.A),
    CURRENT_HARMONIC_L3_H3(23, "I3-H3", UnitSymbol.A),
    CURRENT_HARMONIC_L1_H5(24, "I1-H5", UnitSymbol.A),
    CURRENT_HARMONIC_L2_H5(25, "I2-H5", UnitSymbol.A),
    CURRENT_HARMONIC_L3_H5(26, "I3-H5", UnitSymbol.A),
    CURRENT_HARMONIC_L1_H7(27, "I1-H7", UnitSymbol.A),
    CURRENT_HARMONIC_L2_H7(28, "I2-H7", UnitSymbol.A),
    CURRENT_HARMONIC_L3_H7(29, "I3-H7", UnitSymbol.A),
    CURRENT_HARMONIC_L1_H9(30, "I1-H9", UnitSymbol.A),
    CURRENT_HARMONIC_L2_H9(31, "I2-H9", UnitSymbol.A),
    CURRENT_HARMONIC_L3_H9(32, "I3-H9", UnitSymbol.A),
    CURRENT_HARMONIC_L1_H11(33, "I1-H11", UnitSymbol.A),
    CURRENT_HARMONIC_L2_H11(34, "I2-H11", UnitSymbol.A),
    CURRENT_HARMONIC_L3_H11(35, "I3-H11", UnitSymbol.A),
    CURRENT_HARMONIC_L1_H13(36, "I1-H13", UnitSymbol.A),
    CURRENT_HARMONIC_L2_H13(37, "I2-H13", UnitSymbol.A),
    CURRENT_HARMONIC_L3_H13(38, "I3-H13", UnitSymbol.A),
    CURRENT_HARMONIC_L1_H15(39, "I1-H15", UnitSymbol.A),
    CURRENT_HARMONIC_L2_H15(40, "I2-H15", UnitSymbol.A),
    CURRENT_HARMONIC_L3_H15(41, "I3-H15", UnitSymbol.A);

    private static final Map<Integer, LsPeakShavingMeasurementType> MEASUREMENTS_MAP = Arrays
            .stream(LsPeakShavingMeasurementType.values())
            .collect(Collectors.toMap(LsPeakShavingMeasurementType::getIndex, e -> e));

    private int index;
    private String description;
    private UnitSymbol unitSymbol;
    private UnitMultiplier unitMultiplier;

    LsPeakShavingMeasurementType(final int index, final String description, final UnitSymbol unitSymbol) {
        this(index, description, unitSymbol, UnitMultiplier.none);
    }

    LsPeakShavingMeasurementType(final int index, final String description, final UnitSymbol unitSymbol,
            final UnitMultiplier unitMultiplier) {
        this.index = index;
        this.description = description;
        this.unitSymbol = unitSymbol;
        this.unitMultiplier = unitMultiplier;
    }

    public static LsPeakShavingMeasurementType getMeasurementType(final int index) {
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
