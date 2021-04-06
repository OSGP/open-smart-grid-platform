/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public enum PacketSwitchedStatusDto {

    INACTIVE(0),
    GPRS(1),
    EDGE(2),
    UMTS(3),
    HSDPA(4),
    LTE(5),
    CDMA(6),
    LTE_CAT_M_18(7),
    LTE_NB_IOT(8),
    RESERVED(9); // 9 - 255

    private final int index;

    private PacketSwitchedStatusDto(final int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public static PacketSwitchedStatusDto fromValue(final int value) {
        for (final PacketSwitchedStatusDto status : PacketSwitchedStatusDto.values()) {
            if (status.index == value) {
                return status;
            }
        }
        return RESERVED;
    }
}
