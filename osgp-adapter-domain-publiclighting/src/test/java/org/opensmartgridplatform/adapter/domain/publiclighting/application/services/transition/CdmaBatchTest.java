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
import org.opensmartgridplatform.domain.core.valueobjects.CdmaDevice;

public class CdmaBatchTest {

    private final InetAddress loopbackAddress = InetAddress.getLoopbackAddress();

    @Test
    public void newCdmaBatch() {
        final CdmaDevice cd1 = new CdmaDevice("cd1", this.loopbackAddress, "200/1", (short) 1);
        final CdmaDevice cd2 = new CdmaDevice("cd2", this.loopbackAddress, "200/1", (short) 1);

        final CdmaBatch batch = new CdmaBatch(cd1);
        batch.addCdmaDevice(cd2);

        Assert.assertEquals(batch.getBatchNumber(), cd1.getBatchNumber());
        Assert.assertEquals(batch.getCdmaBatchDevices().size(), 2);
    }

    @Test
    public void rejectDifferentBatchNumbers() {
        final CdmaDevice cd1 = new CdmaDevice("cd1", this.loopbackAddress, "200/1", (short) 1);
        final CdmaDevice cd2 = new CdmaDevice("cd2", this.loopbackAddress, "200/1", (short) 2);

        final CdmaBatch batch = new CdmaBatch(cd1);
        try {
            batch.addCdmaDevice(cd2);
            Assert.fail("Different batch numbers should be rejected");
        } catch (final IllegalArgumentException e) {
            // Test successful, because different the batch number of all
            // devices in a batch have to be the same.
        }
    }

    @Test
    public void equalsWhenBatchNumbersMatch() {
        final CdmaDevice cdBatch1 = new CdmaDevice("cd1", this.loopbackAddress, "200/1", (short) 1);
        final CdmaDevice cdBatch2 = new CdmaDevice("cd2", this.loopbackAddress, "333/5", (short) 1);

        final CdmaBatch batch1 = new CdmaBatch(cdBatch1);
        final CdmaBatch batch2 = new CdmaBatch(cdBatch2);
        Assert.assertEquals("Batches with the same batch number should be equal", batch1, batch2);
    }

    @Test
    public void largerWhenBatchNumberLarger() {
        final CdmaDevice cdBatch1 = new CdmaDevice("cd1", this.loopbackAddress, "200/1", (short) 3);
        final CdmaDevice cdBatch2 = new CdmaDevice("cd2", this.loopbackAddress, "200/1", (short) 2);

        final CdmaBatch batch1 = new CdmaBatch(cdBatch1);
        final CdmaBatch batch2 = new CdmaBatch(cdBatch2);
        Assert.assertNotEquals("Batches with different batch numbers should not be equal", batch1, batch2);
        Assert.assertTrue(batch1.compareTo(batch2) > 0);
    }

}
