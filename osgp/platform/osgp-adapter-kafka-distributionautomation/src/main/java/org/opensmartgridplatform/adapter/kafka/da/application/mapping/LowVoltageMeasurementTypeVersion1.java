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
  VOLTAGE_L1(0, "U-L1-E", UnitSymbol.V),
  VOLTAGE_L2(1, "U-L2-E", UnitSymbol.V),
  VOLTAGE_L3(2, "U-L3-E", UnitSymbol.V),
  CURRENT_L1(3, "I-L1", UnitSymbol.A),
  CURRENT_L2(4, "I-L2", UnitSymbol.A),
  CURRENT_L3(5, "I-L3", UnitSymbol.A),
  TOTAL_ACTIVE_POWER(6, "SomP", UnitSymbol.W, UnitMultiplier.k),
  TOTAL_REACTIVE_POWER(7, "SomQ", UnitSymbol.VAr, UnitMultiplier.k),
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
  CURRENT_HARMONIC_I1_H3(20, "I1-H3", UnitSymbol.A),
  CURRENT_HARMONIC_I2_H3(21, "I2-H3", UnitSymbol.A),
  CURRENT_HARMONIC_I3_H3(22, "I3-H3", UnitSymbol.A),
  CURRENT_HARMONIC_I1_H5(23, "I1-H5", UnitSymbol.A),
  CURRENT_HARMONIC_I2_H5(24, "I2-H5", UnitSymbol.A),
  CURRENT_HARMONIC_I3_H5(25, "I3-H5", UnitSymbol.A),
  CURRENT_HARMONIC_I1_H7(26, "I1-H7", UnitSymbol.A),
  CURRENT_HARMONIC_I2_H7(27, "I2-H7", UnitSymbol.A),
  CURRENT_HARMONIC_I3_H7(28, "I3-H7", UnitSymbol.A),
  CURRENT_HARMONIC_I1_H9(29, "I1-H9", UnitSymbol.A),
  CURRENT_HARMONIC_I2_H9(30, "I2-H9", UnitSymbol.A),
  CURRENT_HARMONIC_I3_H9(31, "I3-H9", UnitSymbol.A),
  CURRENT_HARMONIC_I1_H11(32, "I1-H11", UnitSymbol.A),
  CURRENT_HARMONIC_I2_H11(33, "I2-H11", UnitSymbol.A),
  CURRENT_HARMONIC_I3_H11(34, "I3-H11", UnitSymbol.A),
  CURRENT_HARMONIC_I1_H13(35, "I1-H13", UnitSymbol.A),
  CURRENT_HARMONIC_I2_H13(36, "I2-H13", UnitSymbol.A),
  CURRENT_HARMONIC_I3_H13(37, "I3-H13", UnitSymbol.A),
  CURRENT_HARMONIC_I1_H15(38, "I1-H15", UnitSymbol.A),
  CURRENT_HARMONIC_I2_H15(39, "I2-H15", UnitSymbol.A),
  CURRENT_HARMONIC_I3_H15(40, "I3-H15", UnitSymbol.A);

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
