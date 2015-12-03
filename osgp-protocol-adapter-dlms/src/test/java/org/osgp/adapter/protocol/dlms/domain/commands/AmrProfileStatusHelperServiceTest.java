package org.osgp.adapter.protocol.dlms.domain.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.alliander.osgp.dto.valueobjects.smartmetering.AmrProfileStatus;

public class AmrProfileStatusHelperServiceTest {

    private final AmrProfileStatusHelperService helperService = new AmrProfileStatusHelperService();

    @Test
    public void testConvertToLong() {
        final Set<AmrProfileStatus> amrStatusses = new HashSet<>();

        amrStatusses.add(AmrProfileStatus.DATA_NOT_VALID);
        amrStatusses.add(AmrProfileStatus.POWER_DOWN);

        assertEquals(132L, (long) this.helperService.toLongValue(amrStatusses));
    }

    @Test
    public void testConvertToAmrProfileStatuss() {
        final long registerValue = Long.parseLong("00100100", 2);

        final Set<AmrProfileStatus> amrStatusses = this.helperService.toAmrProfileStatusses(registerValue);

        assertTrue(amrStatusses.contains(AmrProfileStatus.DATA_NOT_VALID));
        assertTrue(amrStatusses.contains(AmrProfileStatus.CLOCK_ADJUSTED));
    }

    @Test
    public void testBitPositions() {
        assertEquals(1, (int) this.helperService.toBitPosition(AmrProfileStatus.CLOCK_INVALID));
        assertEquals(3, (int) this.helperService.toBitPosition(AmrProfileStatus.DAYLIGHT_SAVING));
        assertEquals(7, (int) this.helperService.toBitPosition(AmrProfileStatus.POWER_DOWN));
    }
}
