/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import com.alliander.data.scadameasurementpublishedevent.UnitMultiplier;
import com.alliander.data.scadameasurementpublishedevent.UnitSymbol;

public enum LowVoltageMeasurementTypeVersion2 implements LowVoltageMeasurementDefinition {
  VOLTAGE_AVERAGE(0, "U-avg", UnitSymbol.V),
  CURRENT_L1(1, "I-L1", UnitSymbol.A),
  CURRENT_L2(2, "I-L2", UnitSymbol.A),
  CURRENT_L3(3, "I-L3", UnitSymbol.A),
  TOTAL_ACTIVE_POWER(4, "Tot-P", UnitSymbol.W, UnitMultiplier.k),
  TOTAL_REACTIVE_POWER(5, "Tot-Q", UnitSymbol.VAr, UnitMultiplier.k),
  ACTIVE_POWER_L1(6, "P-L1", UnitSymbol.W, UnitMultiplier.k),
  ACTIVE_POWER_L2(7, "P-L2", UnitSymbol.W, UnitMultiplier.k),
  ACTIVE_POWER_L3(8, "P-L3", UnitSymbol.W, UnitMultiplier.k),
  REACTIVE_POWER_L1(9, "Q-L1", UnitSymbol.VAr, UnitMultiplier.k),
  REACTIVE_POWER_L2(10, "Q-L2", UnitSymbol.VAr, UnitMultiplier.k),
  REACTIVE_POWER_L3(11, "Q-L3", UnitSymbol.VAr, UnitMultiplier.k),
  POWER_FACTOR_L1(12, "PF-L1", UnitSymbol.none),
  POWER_FACTOR_L2(13, "PF-L2", UnitSymbol.none),
  POWER_FACTOR_L3(14, "PF-L3", UnitSymbol.none),
  CURRENT_TOTAL_HARMONIC_DISTORTION_L1(15, "THDi-L1", UnitSymbol.PerCent),
  CURRENT_TOTAL_HARMONIC_DISTORTION_L2(16, "THDi-L2", UnitSymbol.PerCent),
  CURRENT_TOTAL_HARMONIC_DISTORTION_L3(17, "THDi-L3", UnitSymbol.PerCent),
  CURRENT_HARMONIC_H3_I1(18, "H3-I1", UnitSymbol.A),
  CURRENT_HARMONIC_H3_I2(19, "H3-I2", UnitSymbol.A),
  CURRENT_HARMONIC_H3_I3(20, "H3-I3", UnitSymbol.A),
  CURRENT_HARMONIC_H5_I1(21, "H5-I1", UnitSymbol.A),
  CURRENT_HARMONIC_H5_I2(22, "H5-I2", UnitSymbol.A),
  CURRENT_HARMONIC_H5_I3(23, "H5-I3", UnitSymbol.A),
  CURRENT_HARMONIC_H7_I1(24, "H7-I1", UnitSymbol.A),
  CURRENT_HARMONIC_H7_I2(25, "H7-I2", UnitSymbol.A),
  CURRENT_HARMONIC_H7_I3(26, "H7-I3", UnitSymbol.A),
  CURRENT_HARMONIC_H9_I1(27, "H9-I1", UnitSymbol.A),
  CURRENT_HARMONIC_H9_I2(28, "H9-I2", UnitSymbol.A),
  CURRENT_HARMONIC_H9_I3(29, "H9-I3", UnitSymbol.A),
  CURRENT_HARMONIC_H11_I1(30, "H11-I1", UnitSymbol.A),
  CURRENT_HARMONIC_H11_I2(31, "H11-I2", UnitSymbol.A),
  CURRENT_HARMONIC_H11_I3(32, "H11-I3", UnitSymbol.A),
  CURRENT_HARMONIC_H13_I1(33, "H13-I1", UnitSymbol.A),
  CURRENT_HARMONIC_H13_I2(34, "H13-I2", UnitSymbol.A),
  CURRENT_HARMONIC_H13_I3(35, "H13-I3", UnitSymbol.A),
  CURRENT_HARMONIC_H15_I1(36, "H15-I1", UnitSymbol.A),
  CURRENT_HARMONIC_H15_I2(37, "H15-I2", UnitSymbol.A),
  CURRENT_HARMONIC_H15_I3(38, "H15-I3", UnitSymbol.A),
  RMS_CURRENT_N0(39, "IrmsN", UnitSymbol.A),
  P_PLUS(40, "Pp", UnitSymbol.none),
  P_MIN(41, "Pm", UnitSymbol.none),
  Q_PLUS(42, "Qp", UnitSymbol.none),
  Q_MIN(43, "Qm", UnitSymbol.none),
  VOLTAGE_RMS_L1(44, "U-L1", UnitSymbol.V),
  VOLTAGE_RMS_L2(45, "U-L2", UnitSymbol.V),
  VOLTAGE_RMS_L3(46, "U-L3", UnitSymbol.V),
  TEMPERATURE(47, "Temp", UnitSymbol.C),
  FREQUENCY(48, "F", UnitSymbol.Hz);

  private int index;
  private String description;
  private UnitSymbol unitSymbol;
  private UnitMultiplier unitMultiplier;

  LowVoltageMeasurementTypeVersion2(
      final int index, final String description, final UnitSymbol unitSymbol) {
    this(index, description, unitSymbol, UnitMultiplier.none);
  }

  LowVoltageMeasurementTypeVersion2(
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
