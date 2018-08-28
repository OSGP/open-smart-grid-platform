/**

 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;

import org.junit.Test;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaBatch;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaBatchDevice;

public class CdmaBatchTest {

    private final InetAddress loopbackAddress = InetAddress.getLoopbackAddress();

    @Test(expected = IllegalArgumentException.class)
    public void newBatchNumberNull() {
        new CdmaBatch(null);
    }

    @Test
    public void newCdmaBatch() {
        final CdmaBatch batch = new CdmaBatch((short) 1);

        final CdmaBatchDevice cd1 = new CdmaBatchDevice("cd1", this.loopbackAddress);
        batch.addCdmaBatchDevice(cd1);

        final CdmaBatchDevice cd2 = new CdmaBatchDevice("cd2", this.loopbackAddress);
        batch.addCdmaBatchDevice(cd2);

        assertEquals(Short.valueOf((short) 1), batch.getBatchNumber());
        assertEquals("Batch should contain 2 devices", 2, batch.getCdmaBatchDevices().size());
    }

    @Test
    public void equalsWhenBatchNumbersMatch() {
        final CdmaBatch batch1 = new CdmaBatch((short) 7);
        final CdmaBatch batch2 = new CdmaBatch((short) 7);

        assertEquals("Batches with the same batch number should be equal", batch1, batch2);
    }

    @Test
    public void largerWhenBatchNumberLarger() {
        final CdmaBatch batch3 = new CdmaBatch((short) 3);
        final CdmaBatch batch2 = new CdmaBatch((short) 2);
        assertNotEquals("Batches with different batch numbers should not be equal", batch3, batch2);
        assertTrue(batch3.compareTo(batch2) > 0);
    }
}
