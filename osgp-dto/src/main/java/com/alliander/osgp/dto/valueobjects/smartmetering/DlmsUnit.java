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
 * @author dev
 */
public enum DlmsUnit {

    /**
     * year
     */
    a(1),
    /**
     * month
     */
    mo(2),
    /**
     * week
     */
    wk(3),
    /**
     * day
     */
    d(4),
    /**
     * hour
     */
    h(5),
    /**
     * minute
     */
    min(6),
    /**
     * second
     */
    s(7),
    /**
     * meter
     */
    m(11),
    /**
     * cubic meter
     */
    m3(13),
    /**
     * corrected cubic meter
     */
    m3cor(14),
    /**
     * liter
     */
    l(19),
    /**
     * watt
     */
    w(27),
    /**
     * watt hour
     */
    wh(30),
    /**
     * ampere
     */
    amp(33),
    /**
     * voltage
     */
    v(35),
    /**
     * ohm
     */
    o(38);

    private final int dlmsEnum;

    private DlmsUnit(int dlmsEnum) {
        this.dlmsEnum = dlmsEnum;
    }

    public int getDlmsEnum() {
        return this.dlmsEnum;
    }

    public static DlmsUnit fromDlmsEnum(int dlmsEnum) {
        for (DlmsUnit dlmsUnit : values()) {
            if (dlmsUnit.getDlmsEnum() == dlmsEnum) {
                return dlmsUnit;
            }
        }
        throw new IllegalArgumentException(String.format("%s is a not supported dlms enum value", dlmsEnum));
    }

}
