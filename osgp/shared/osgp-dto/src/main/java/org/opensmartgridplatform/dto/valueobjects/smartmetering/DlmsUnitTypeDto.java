/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.HashMap;
import java.util.Map;

/**
 * This class maps all possible Dlms units with the corrresponding string that are available as an
 * enum in @see OsgpUnitType. The index property corresponds with the 'unit' in the Blue book. The
 * 'unit' string property corresponds with the enum in OsgpUnitType.
 */
public enum DlmsUnitTypeDto {
  UNDEFINED(0, "UNDEFINED"),
  YEAR(1, "Y"),
  MONTH(2, "MO"),
  WEEK(3, "WK"),
  DAY(4, "D"),
  HOUR(5, "H"),
  MIN(6, "MIN"),
  SECOND(7, "SD"),
  DEGREE(8, "DEGREE"),
  DEGREE_CELCIUS(9, "DEGREE_CELCIUS"),
  CURRENCY(10, "CURRENCY"),
  METER(11, "M"),
  METER_PER_SECOND(12, "METER_PER_SECOND"),
  M3(13, "M_3"), // this underscore is needed because the xsd generates this!
  M3_CORR(14, "M_3"),
  M3_FLUX(15, "M_3_FLUX"),
  M3_FLUX_CORR(16, "M_3_FLUX_CORR"),
  VOLUME_FLUX(17, "VOLUME_FLUX"),
  VOLUME_FLUX_CORR(18, "VOLUME_FLUX_CORR"),
  LITRE(19, "L"),
  KILOGRAM(20, "KILOGRAM"),
  NEWTON(21, "NEWTON"),
  NEWTON_METER(22, "NEWTON_METER"),
  PASCAL(23, "PASCAL"),
  BAR(24, "BAR"),
  JOULE(25, "J"),
  JOULE_PER_HOUR(26, "JOULE_PER_HOUR"),
  WATT(27, "W"),
  VOLT_AMPERE(28, "VOLT_AMPERE"),
  VAR(29, "VAR"),
  KWH(30, "KWH"),
  VOLT_AMP_HOUR(31, "VOLT_AMP_HOUR"),
  VAR_HOUR(32, "VAR_HOUR"),
  AMPERE(33, "AMP", "A"),
  COULOMB(34, "COULOMB"),
  VOLT(35, "V"),
  VOLT_PER_METER(36, "VOLT_PER_METER"),
  FARAD(37, "FARAD"),
  OHM(38, "O"),
  RESTISTIVITY(39, "RESTISTIVITY"),
  WEBER(40, "WEBER"),
  TESLA(41, "TESLA"),
  AMP_PER_METER(42, "AMP_PER_METER"),
  HENRY(43, "HENRY"),
  HERTZ(44, "HERTZ"),
  ACTIVE_ENERGY(45, "ACTIVE_ENERGY"),
  REACTIVE_ENERGY(46, "REACTIVE_ENERGY"),
  APPARENT_ENERGY(47, "APPARENT_ENERGY"),
  VOLT_SQUARED_HOURS(48, "VOLT_SQUARED_HOURS"),
  AMP_SQUARED_HOURS(49, "AMP_SQUARED_HOURS"),
  KG_PER_SECOND(50, "KG_PER_SECOND"),
  SIEMENS(51, "SIEMENS"),
  KELVIN(52, "KELVIN"),
  VOLT_PULSE_VALUE(53, "VOLT_PULSE_VALUE"),
  AMP_PULSE_VALUE(54, "AMP_PULSE_VALUE"),
  VOLUME(55, "VOLUME"),
  PERCENTAGE(56, "PERCENTAGE"),
  AMP_HOUR(57, "AMP_HOUR"),
  ENGERY(60, "ENGERY"),
  WOBBE(61, "WOBBE"),
  MOLE_PERCENT(62, "MOLE_PERCENT"),
  MASS_DENSITY(63, "MASS_DENSITY"),
  PASCAL_SECOND(64, "PASCAL_SECOND"),
  JOULE_KG(65, "JOULE_KG"),
  DB_MILLIWAT(70, "DB_MILLIWAT"),
  DB_MICROVOLT(71, "DB_MICROVOLT"),
  DB(72, "DB"),
  COUNT(255, "COUNT");

  private static final Map<Integer, DlmsUnitTypeDto> INDEX_TO_TYPE_MAP = new HashMap<>();
  private static final Map<String, DlmsUnitTypeDto> UNIT_TO_TYPE_MAP = new HashMap<>();

  static {
    for (final DlmsUnitTypeDto unitType : DlmsUnitTypeDto.values()) {
      INDEX_TO_TYPE_MAP.put(unitType.getIndex(), unitType);
    }
  }

  static {
    for (final DlmsUnitTypeDto unitType : DlmsUnitTypeDto.values()) {
      UNIT_TO_TYPE_MAP.put(unitType.getUnitShort(), unitType);
    }
  }

  private final int index;
  private final String unit;
  private final String unitShort;

  private DlmsUnitTypeDto(final int index, final String unit) {
    this.index = index;
    this.unit = unit;
    this.unitShort = unit;
  }

  private DlmsUnitTypeDto(final int index, final String unit, final String unitShort) {
    this.index = index;
    this.unit = unit;
    this.unitShort = unitShort;
  }

  public static Map<Integer, DlmsUnitTypeDto> getIndexToTypeMap() {
    return INDEX_TO_TYPE_MAP;
  }

  public static DlmsUnitTypeDto getUnitType(final int index) {
    return INDEX_TO_TYPE_MAP.get(index & 0xFF);
  }

  public static DlmsUnitTypeDto getUnitType(final String unit) {
    return UNIT_TO_TYPE_MAP.get(unit);
  }

  public static String getUnit(final int index) {
    final DlmsUnitTypeDto unitType = getUnitType(index);
    return unitType == null ? UNDEFINED.getUnit() : unitType.getUnit();
  }

  public int getIndex() {
    return this.index;
  }

  public String getUnit() {
    return this.unit;
  }

  public String getUnitShort() {
    return this.unitShort;
  }
}
