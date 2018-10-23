/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import org.opensmartgridplatform.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlagDto;

public class AmrProfileStatusHelperServiceTest {

    private final AmrProfileStatusCodeHelperService helperService = new AmrProfileStatusCodeHelperService();

    @Test
    public void testConvertToLong() {
        final Set<AmrProfileStatusCodeFlagDto> amrStatusCodeFlags = new HashSet<>();

        amrStatusCodeFlags.add(AmrProfileStatusCodeFlagDto.DATA_NOT_VALID);
        amrStatusCodeFlags.add(AmrProfileStatusCodeFlagDto.POWER_DOWN);

        assertEquals((short) 132, (short) this.helperService.toValue(amrStatusCodeFlags));
    }

    @Test
    public void testConvertToAmrProfileStatusCodeFlags() {
        final short registerValue = Short.parseShort("00100100", 2);

        final Set<AmrProfileStatusCodeFlagDto> amrStatusCodeFlags = this.helperService
                .toAmrProfileStatusCodeFlags(registerValue);

        assertTrue(amrStatusCodeFlags.contains(AmrProfileStatusCodeFlagDto.DATA_NOT_VALID));
        assertTrue(amrStatusCodeFlags.contains(AmrProfileStatusCodeFlagDto.CLOCK_ADJUSTED));
    }

    @Test
    public void testBitPositions() {
        assertEquals(1, (int) this.helperService.toBitPosition(AmrProfileStatusCodeFlagDto.CLOCK_INVALID));
        assertEquals(3, (int) this.helperService.toBitPosition(AmrProfileStatusCodeFlagDto.DAYLIGHT_SAVING));
        assertEquals(7, (int) this.helperService.toBitPosition(AmrProfileStatusCodeFlagDto.POWER_DOWN));
    }
}
