/**

 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;

import org.junit.Assert;
import org.junit.Test;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaBatch;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaBatchDevice;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaMastSegment;

public class CdmaMastSegmentTest {

    private final InetAddress loopbackAddress = InetAddress.getLoopbackAddress();

    @Test(expected = IllegalArgumentException.class)
    public void newNameNull() {
        new CdmaMastSegment(null);
    }

    @Test
    public void newBatchNumberNull() {
        final CdmaMastSegment mastSegment = new CdmaMastSegment("200/1");

        final CdmaBatchDevice cd1 = new CdmaBatchDevice("cd1", this.loopbackAddress);
        mastSegment.addCdmaBatchDevice(null, cd1);
        final CdmaBatch cdmaBatch = mastSegment.popCdmaBatch();
        assertEquals("Batch should get maximum batch number", CdmaBatch.MAX_BATCH_NUMBER, cdmaBatch.getBatchNumber());
    }

    @Test
    public void newCdmaMastSegment() {
        final CdmaMastSegment mastSegment = new CdmaMastSegment("200/1");

        final CdmaBatchDevice cd1 = new CdmaBatchDevice("cd1", this.loopbackAddress);
        mastSegment.addCdmaBatchDevice((short) 1, cd1);

        assertEquals("200/1", mastSegment.getMastSegment());
        assertFalse("MastSegment should contain 1 device", mastSegment.empty());
    }

    @Test
    public void popCdmaBatch() {
        final CdmaMastSegment mastSegment = new CdmaMastSegment("200/1");

        final CdmaBatchDevice cd1 = new CdmaBatchDevice("cd1", this.loopbackAddress);
        mastSegment.addCdmaBatchDevice((short) 1, cd1);

        final CdmaBatchDevice cd2 = new CdmaBatchDevice("cd2", this.loopbackAddress);
        mastSegment.addCdmaBatchDevice((short) 1, cd2);

        final CdmaBatch batch = mastSegment.popCdmaBatch();
        assertEquals(2, batch.getCdmaBatchDevices().size());

        // popCdmaBatch should remove the batch from the mast segment,
        // there should be no more batch left in the mast segment.
        assertTrue("MastSegment should not contain any devices", mastSegment.empty());
    }

    @Test
    public void equalsWhenSegmentNameMatch() {
        final CdmaMastSegment mastSegment1 = new CdmaMastSegment("200/1");
        final CdmaMastSegment mastSegment2 = new CdmaMastSegment("200/1");
        assertEquals("Mast segments with the same name should be equal", mastSegment1, mastSegment2);
    }

    @Test
    public void testSegmentNameDefaultIsLastItem() {
        final CdmaMastSegment mastSegmentDefault = new CdmaMastSegment(CdmaMastSegment.DEFAULT_MASTSEGMENT);
        final CdmaMastSegment mastSegmentNormal = new CdmaMastSegment("zzzMast");

        assertTrue("An empty mast segment should be bigger than all other mast segments",
                mastSegmentDefault.compareTo(mastSegmentNormal) > 0);
    }

    @Test
    public void testPopBatches() {
        final CdmaMastSegment mastSegment = new CdmaMastSegment("200/55");
        for (short i = 0; i < 10; i++) {
            // Each device has a different batch
            mastSegment.addCdmaBatchDevice(i, new CdmaBatchDevice("cd" + i, this.loopbackAddress));
        }

        for (int batchNo = 0; batchNo < 10; batchNo++) {
            Assert.assertFalse("There should be at least one CdmaBatch left", mastSegment.empty());
            mastSegment.popCdmaBatch();
        }
        assertTrue("All CdmaBatches should have been removed by now", mastSegment.empty());
    }
}
