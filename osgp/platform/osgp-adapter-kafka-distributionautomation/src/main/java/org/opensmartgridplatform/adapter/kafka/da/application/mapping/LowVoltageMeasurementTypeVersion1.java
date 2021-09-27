/*
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import com.alliander.data.scadameasurementpublishedevent.UnitMultiplier;
import com.alliander.data.scadameasurementpublishedevent.UnitSymbol;

public enum LowVoltageMeasurementTypeVersion1 implements LowVoltageMeasurementDefinition {
  VOLTAGE_L1(0, "U-L1", UnitSymbol.V),
  VOLTAGE_L2(1, "U-L2", UnitSymbol.V),
  VOLTAGE_L3(2, "U-L3", UnitSymbol.V),
  CURRENT_L1(3, "I-L1", UnitSymbol.A),
  CURRENT_L2(4, "I-L2", UnitSymbol.A),
  CURRENT_L3(5, "I-L3", UnitSymbol.A),
  TOTAL_ACTIVE_POWER(6, "Tot-P", UnitSymbol.W, UnitMultiplier.k),
  TOTAL_REACTIVE_POWER(7, "Tot-Q", UnitSymbol.VAr, UnitMultiplier.k),
  ACTIVE_POWER_L1(8, "P-L1", UnitSymbol.W, UnitMultiplier.k),
  ACTIVE_POWER_L2(9, "P-L2", UnitSymbol.W, UnitMultiplier.k),
  ACTIVE_POWER_L3(10, "P-L3", UnitSymbol.W, UnitMultiplier.k),
  REACTIVE_POWER_L1(11, "Q-L1", UnitSymbol.VAr, UnitMultiplier.k),
  REACTIVE_POWER_L2(12, "Q-L2", UnitSymbol.VAr, UnitMultiplier.k),
  REACTIVE_POWER_L3(13, "Q-L3", UnitSymbol.VAr, UnitMultiplier.k),
  POWER_FACTOR_L1(14, "PF-L1", UnitSymbol.none),
  POWER_FACTOR_L2(15, "PF-L2", UnitSymbol.none),
  POWER_FACTOR_L3(16, "PF-L3", UnitSymbol.none),
  CURRENT_TOTAL_HARMONIC_DISTORTION_L1(17, "THDi-L1", UnitSymbol.PerCent),
  CURRENT_TOTAL_HARMONIC_DISTORTION_L2(18, "THDi-L2", UnitSymbol.PerCent),
  CURRENT_TOTAL_HARMONIC_DISTORTION_L3(19, "THDi-L3", UnitSymbol.PerCent),
  CURRENT_HARMONIC_H3_I1(20, "H3-I1", UnitSymbol.A),
  CURRENT_HARMONIC_H3_I2(21, "H3-I2", UnitSymbol.A),
  CURRENT_HARMONIC_H3_I3(22, "H3-I3", UnitSymbol.A),
  CURRENT_HARMONIC_H5_I1(23, "H5-I1", UnitSymbol.A),
  CURRENT_HARMONIC_H5_I2(24, "H5-I2", UnitSymbol.A),
  CURRENT_HARMONIC_H5_I3(25, "H5-I3", UnitSymbol.A),
  CURRENT_HARMONIC_H7_I1(26, "H7-I1", UnitSymbol.A),
  CURRENT_HARMONIC_H7_I2(27, "H7-I2", UnitSymbol.A),
  CURRENT_HARMONIC_H7_I3(28, "H7-I3", UnitSymbol.A),
  CURRENT_HARMONIC_H9_I1(29, "H9-I1", UnitSymbol.A),
  CURRENT_HARMONIC_H9_I2(30, "H9-I2", UnitSymbol.A),
  CURRENT_HARMONIC_H9_I3(31, "H9-I3", UnitSymbol.A),
  CURRENT_HARMONIC_H11_I1(32, "H11-I1", UnitSymbol.A),
  CURRENT_HARMONIC_H11_I2(33, "H11-I2", UnitSymbol.A),
  CURRENT_HARMONIC_H11_I3(34, "H11-I3", UnitSymbol.A),
  CURRENT_HARMONIC_H13_I1(35, "H13-I1", UnitSymbol.A),
  CURRENT_HARMONIC_H13_I2(36, "H13-I2", UnitSymbol.A),
  CURRENT_HARMONIC_H13_I3(37, "H13-I3", UnitSymbol.A),
  CURRENT_HARMONIC_H15_I1(38, "H15-I1", UnitSymbol.A),
  CURRENT_HARMONIC_H15_I2(39, "H15-I2", UnitSymbol.A),
  CURRENT_HARMONIC_H15_I3(40, "H15-I3", UnitSymbol.A);

  private int index;
  private String description;
  private UnitSymbol unitSymbol;
  private UnitMultiplier unitMultiplier;

  LowVoltageMeasurementTypeVersion1(
      final int index, final String description, final UnitSymbol unitSymbol) {
    this(index, description, unitSymbol, UnitMultiplier.none);
  }

  LowVoltageMeasurementTypeVersion1(
      final int index,
      final String description,
      final UnitSymbol unitSymbol,
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
