/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public enum PacketSwitchedStatusDto {

    INACTIVE,
    GPRS,
    EDGE,
    UMTS,
    HSDPA,
    LTE,
    CDMA,
    LTE_CAT_M_18,
    LTE_NB_IOT,
    RESERVED;

    public String value() {
        return name();
    }

    public static PacketSwitchedStatusDto fromValue(String v) {
        return valueOf(v);
    }

}
