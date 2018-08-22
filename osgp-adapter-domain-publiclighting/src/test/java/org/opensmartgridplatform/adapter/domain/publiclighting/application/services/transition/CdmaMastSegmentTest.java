/**

 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.services.transition;

import java.net.InetAddress;

import org.junit.Assert;
import org.junit.Test;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaBatch;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaMastSegment;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaDevice;

public class CdmaMastSegmentTest {

    private final InetAddress loopbackAddress = InetAddress.getLoopbackAddress();

    @Test
    public void newCdmaMastSegment() {
        final CdmaDevice cd1 = new CdmaDevice("cd1", this.loopbackAddress, "200/1", (short) 1);
        final CdmaMastSegment mastSegment = new CdmaMastSegment(cd1);

        Assert.assertEquals(mastSegment.getMastSegment(), "200/1");
        Assert.assertFalse(mastSegment.empty());
    }

    @Test
    public void popCdmaBatch() {
        final CdmaDevice cd1 = new CdmaDevice("cd1", this.loopbackAddress, "200/1", (short) 1);
        final CdmaDevice cd2 = new CdmaDevice("cd2", this.loopbackAddress, "200/1", (short) 1);
        final CdmaMastSegment mastSegment = new CdmaMastSegment(cd1);
        mastSegment.addCdmaDevice(cd2);

        final CdmaBatch batch = mastSegment.popCdmaBatch();
        Assert.assertEquals(batch.getCdmaBatchDevices().size(), 2);

        // popCdmaBatch should remove the batch from the mast segment,
        // there should be no more batch left in the mast segment.
        Assert.assertTrue(mastSegment.empty());
    }

    @Test
    public void equalsWhenSegmentNameMatch() {
        final CdmaDevice cd1Mast1 = new CdmaDevice("cd1", this.loopbackAddress, "200/1", (short) 1);
        final CdmaDevice cd2Mast1 = new CdmaDevice("cd2", this.loopbackAddress, "200/1", (short) 3);

        final CdmaMastSegment mastSegment1 = new CdmaMastSegment(cd1Mast1);
        final CdmaMastSegment mastSegment2 = new CdmaMastSegment(cd2Mast1);
        Assert.assertEquals("Mast segments with the same name should be equal", mastSegment1, mastSegment2);
    }

    @Test
    public void testOrder() {
        final CdmaDevice cdNoMast = new CdmaDevice("cdNoMast", this.loopbackAddress, null, (short) 2);
        final CdmaDevice cdMastz = new CdmaDevice("cdMastz", this.loopbackAddress, "zzzMast", (short) 1);

        final CdmaMastSegment mastSegmentDefault = new CdmaMastSegment(cdNoMast);
        final CdmaMastSegment mastSegmentNormal = new CdmaMastSegment(cdMastz);

        Assert.assertTrue("An empty mast segment should be bigger than all other mast segments",
                mastSegmentDefault.compareTo(mastSegmentNormal) > 0);
    }

    @Test
    public void testPopBatches() {
        final CdmaMastSegment mastSegment = new CdmaMastSegment(
                new CdmaDevice("cd0", this.loopbackAddress, "200/55", (short) 0));
        for (short i = 1; i < 10; i++) {
            // Each device has a different batch
            mastSegment.addCdmaDevice(new CdmaDevice("cd" + i, this.loopbackAddress, "200/55", i));
        }

        for (int batchNo = 0; batchNo < 10; batchNo++) {
            Assert.assertFalse("There should be at least one CdmaBatch left", mastSegment.empty());
            mastSegment.popCdmaBatch();
        }
        Assert.assertTrue("All CdmaBatches should have been removed by now", mastSegment.empty());
    }
}
