package org.osgp.adapter.protocol.dlms.domain.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatusCodeFlag;

public class AmrProfileStatusHelperServiceTest {

    private final AmrProfileStatusHelperService helperService = new AmrProfileStatusHelperService();

    @Test
    public void testConvertToLong() {
        final Set<AmrProfileStatusCodeFlag> amrStatusses = new HashSet<>();

        amrStatusses.add(AmrProfileStatusCodeFlag.DATA_NOT_VALID);
        amrStatusses.add(AmrProfileStatusCodeFlag.POWER_DOWN);

        assertEquals((short) 132, (short) this.helperService.toValue(amrStatusses));
    }

    @Test
    public void testConvertToAmrProfileStatuss() {
        final short registerValue = Short.parseShort("00100100", 2);

        final Set<AmrProfileStatusCodeFlag> amrStatusses = this.helperService.toAmrProfileStatusCodeFlags(registerValue);

        assertTrue(amrStatusses.contains(AmrProfileStatusCodeFlag.DATA_NOT_VALID));
        assertTrue(amrStatusses.contains(AmrProfileStatusCodeFlag.CLOCK_ADJUSTED));
    }

    @Test
    public void testBitPositions() {
        assertEquals(1, (int) this.helperService.toBitPosition(AmrProfileStatusCodeFlag.CLOCK_INVALID));
        assertEquals(3, (int) this.helperService.toBitPosition(AmrProfileStatusCodeFlag.DAYLIGHT_SAVING));
        assertEquals(7, (int) this.helperService.toBitPosition(AmrProfileStatusCodeFlag.POWER_DOWN));
    }
}
