/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.valueobjects;

import java.util.HashMap;
import java.util.Map;

/**
 * This class maps all possible Dlms units with the corrresponding string that
 * are available as an enum in @see OsgpUnitType. The index property corresponds
 * with the 'unit' in the Blue book. The 'unit' string property corresponds with
 * the enum in OsgpUnitType. The additional 'quantity' and 'unitName' properties
 * also correspond with the values from the Blue book.
 */
public enum DlmsUnitType {

    UNDEFINED(0, "UNDEFINED", "", ""),
    YEAR(1, "YEAR", "", ""),
    MONTH(2, "MONTH", "", ""),
    WEEK(3, "WEEK", "", ""),
    DAY(4, "DAY", "", ""),
    HOUR(5, "HOUR", "", ""),
    MIN(6, "MIN", "", ""),
    SECOND(7, "SECOND", "", ""),
    DEGREE(8, "DEGREE", "", ""),
    DEGREE_CELCIUS(9, "DEGREE_CELCIUS", "", ""),
    CURRENCY(10, "CURRENCY", "", ""),
    METER(11, "METER", "", ""),
    METER_PER_SECOND(12, "METER_PER_SECOND", "", ""),
    CUBIC_METRE(13, "M_3", "", ""),
    CUBIC_METRE_CORRECTED(14, "M_3", "", ""),
    CUBIC_METRE_FLUX(15, "CUBIC_METRE_FLUX", "", ""),
    CUBIC_METRE_FLUX_CORRECTED(16, "CUBIC_METRE_FLUX_CORRECTED", "", ""),
    VOLUME_FLUX(17, "VOLUME_FLUX", "", ""),
    CORRECTED_VOLUME_FLUX(18, "CORRECTED_VOLUME_FLUX", "", ""),
    LITRE(19, "LITRE", "", ""),
    KILOGRAM(20, "KILOGRAM", "", ""),
    NEWTON(21, "NEWTON", "", ""),
    NEWTON_METER(22, "NEWTON_METER", "", ""),
    PASCAL(23, "PASCAL", "", ""),
    BAR(24, "BAR", "", ""),
    JOULE(25, "JOULE", "", ""),
    JOULE_PER_HOUR(26, "JOULE_PER_HOUR", "", ""),
    WATT(27, "WATT", "", ""),
    VOLT_AMPERE(28, "VOLT_AMPERE", "", ""),
    VAR(29, "VAR", "", ""),
    WATT_HOUR(30, "KWH", "", ""),
    VOLT_AMP_HOUR(31, "VOLT_AMP_HOUR", "", ""),
    VAR_HOUR(32, "VAR_HOUR", "", ""),
    AMPERE(33, "AMPERE", "", ""),
    COULOMB(34, "COULOMB", "", ""),
    VOLT(35, "VOLT", "", ""),
    VOLT_PER_METER(36, "VOLT_PER_METER", "", ""),
    FARAD(37, "FARAD", "", ""),
    OHM(38, "OHM", "", ""),
    RESTISTIVITY(39, "RESTISTIVITY", "", ""),
    WEBER(40, "WEBER", "", ""),
    TESLA(41, "TESLA", "", ""),
    AMP_PER_METER(42, "AMP_PER_METER", "", ""),
    HENRY(43, "HENRY", "", ""),
    HERTZ(44, "HERTZ", "", ""),
    ACTIVE_ENERGY(45, "ACTIVE_ENERGY", "", ""),
    REACTIVE_ENERGY(46, "REACTIVE_ENERGY", "", ""),
    APPARENT_ENERGY(47, "APPARENT_ENERGY", "", ""),
    VOLT_SQUARED_HOURS(48, "VOLT_SQUARED_HOURS", "", ""),
    AMP_SQUARED_HOURS(49, "AMP_SQUARED_HOURS", "", ""),
    KG_PER_SECOND(50, "KG_PER_SECOND", "", ""),
    SIEMENS(51, "SIEMENS", "", ""),
    KELVIN(52, "KELVIN", "", ""),
    VOLT_PULSE_VALUE(53, "VOLT_PULSE_VALUE", "", ""),
    AMP_PULSE_VALUE(54, "AMP_PULSE_VALUE", "", ""),
    VOLUME(55, "VOLUME", "", ""),
    PERCENTAGE(56, "PERCENTAGE", "", ""),
    AMP_HOUR(57, "AMP_HOUR", "", ""),
    ENGERY(60, "ENGERY", "", ""),
    WOBBE(61, "WOBBE", "", ""),
    MOLE_PERCENT(62, "MOLE_PERCENT", "", ""),
    MASS_DENSITY(63, "MASS_DENSITY", "", ""),
    PASCAL_SECOND(64, "UNDEFINED", "", ""),
    JOULE_KG(65, "JOULE_KG", "", ""),
    DB_MILLIWAT(70, "DB_MILLIWAT", "", ""),
    DB_MICROVOLT(71, "DB_MICROVOLT", "", ""),
    DB(72, "DB", "", "");

    private static final Map<Integer, DlmsUnitType> UNIT_TYPES_MAP = new HashMap<Integer, DlmsUnitType>();

    static {
        for (DlmsUnitType unitType : DlmsUnitType.values()) {
            UNIT_TYPES_MAP.put(unitType.getIndex(), unitType);
        }
    }

    private final int index;
    private final String unit;
    private final String quantity;
    private final String unitName;

    private DlmsUnitType(int index, String unit, String quantity, String unitName) {
        this.index = index;
        this.unit = unit;
        this.quantity = quantity;
        this.unitName = unitName;
    }

    public static Map<Integer, DlmsUnitType> getUnitTypesMap() {
        return UNIT_TYPES_MAP;
    }

    public static DlmsUnitType getUnitType(final int index) {
        return UNIT_TYPES_MAP.get(index);
    }

    public static String getUnit(final int index) {
        DlmsUnitType unitType = getUnitType(index);
        return unitType == null ? UNDEFINED.getUnit() : unitType.getUnit();
    }

    public int getIndex() {
        return this.index;
    }

    public String getUnit() {
        return this.unit;
    }

    public String getQuantity() {
        return this.quantity;
    }

    public String getUnitName() {
        return this.unitName;
    }
}
