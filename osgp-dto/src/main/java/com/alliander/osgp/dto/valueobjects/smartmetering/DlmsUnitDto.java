/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

/**
 * supported units of measurement values on dlms devices
 *
 */
public enum DlmsUnitDto {

    /**
     * year
     */
    A(1),
    /**
     * month
     */
    MO(2),
    /**
     * week
     */
    WK(3),
    /**
     * day
     */
    D(4),
    /**
     * hour
     */
    H(5),
    /**
     * minute
     */
    MIN(6),
    /**
     * second
     */
    S(7),
    /**
     * meter
     */
    M(11),
    /**
     * cubic meter
     */
    M3(13),
    /**
     * corrected cubic meter
     */
    M3COR(14),
    /**
     * liter
     */
    L(19),
    /**
     * watt
     */
    W(27),
    /**
     * watt hour
     */
    WH(30),
    /**
     * ampere
     */
    AMP(33),
    /**
     * voltage
     */
    V(35),
    /**
     * ohm
     */
    O(38),
    /**
     * Joule
     */
    J(25),
    /**
     * 0 is interpreted as "unit is not defined" (not part of dlms standard)
     */
    UNDEFINED(0);

    private final int dlmsEnum;

    private DlmsUnitDto(final int dlmsEnum) {
        this.dlmsEnum = dlmsEnum;
    }

    public int getDlmsEnum() {
        return this.dlmsEnum;
    }

    public static DlmsUnitDto fromDlmsEnum(final int dlmsEnum) {
        for (final DlmsUnitDto dlmsUnit : values()) {
            if (dlmsUnit.getDlmsEnum() == dlmsEnum) {
                return dlmsUnit;
            }
        }
        throw new IllegalArgumentException(String.format("%s is a not supported dlms enum value", dlmsEnum));
    }

}
