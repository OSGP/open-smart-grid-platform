//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.publiclighting.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.net.InetAddress;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaBatch;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaBatchDevice;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaMastSegment;

public class CdmaMastSegmentTest {

  private final InetAddress loopbackAddress = InetAddress.getLoopbackAddress();

  @Test
  public void newNameNull() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              new CdmaMastSegment(null);
            });
  }

  @Test
  public void newBatchNumberNull() {
    final CdmaMastSegment mastSegment = new CdmaMastSegment("200/1");

    final CdmaBatchDevice cd1 = new CdmaBatchDevice("cd1", this.loopbackAddress);
    mastSegment.addCdmaBatchDevice(null, cd1);
    final CdmaBatch cdmaBatch = mastSegment.popCdmaBatch();
    assertThat(cdmaBatch.getBatchNumber())
        .withFailMessage("Batch should get maximum batch number")
        .isEqualTo(CdmaBatch.MAX_BATCH_NUMBER);
  }

  @Test
  public void newCdmaMastSegment() {
    final CdmaMastSegment mastSegment = new CdmaMastSegment("200/1");

    final CdmaBatchDevice cd1 = new CdmaBatchDevice("cd1", this.loopbackAddress);
    mastSegment.addCdmaBatchDevice((short) 1, cd1);

    assertThat(mastSegment.getMastSegment()).isEqualTo("200/1");
    assertThat(mastSegment.empty())
        .withFailMessage("MastSegment should contain 1 device")
        .isFalse();
  }

  @Test
  public void popCdmaBatch() {
    final CdmaMastSegment mastSegment = new CdmaMastSegment("200/1");

    final CdmaBatchDevice cd1 = new CdmaBatchDevice("cd1", this.loopbackAddress);
    mastSegment.addCdmaBatchDevice((short) 1, cd1);

    final CdmaBatchDevice cd2 = new CdmaBatchDevice("cd2", this.loopbackAddress);
    mastSegment.addCdmaBatchDevice((short) 1, cd2);

    final CdmaBatch batch = mastSegment.popCdmaBatch();
    assertThat(batch.getCdmaBatchDevices().size()).isEqualTo(2);

    // popCdmaBatch should remove the batch from the mast segment,
    // there should be no more batch left in the mast segment.
    assertThat(mastSegment.empty())
        .withFailMessage("MastSegment should not contain any devices")
        .isTrue();
  }

  @Test
  public void equalsWhenSegmentNameMatch() {
    final CdmaMastSegment mastSegment1 = new CdmaMastSegment("200/1");
    final CdmaMastSegment mastSegment2 = new CdmaMastSegment("200/1");
    assertThat(mastSegment1)
        .withFailMessage("Mast segments with the same name should be equal")
        .isEqualTo(mastSegment2);
  }

  @Test
  public void testSegmentNameDefaultIsLastItem() {
    final CdmaMastSegment mastSegmentDefault =
        new CdmaMastSegment(CdmaMastSegment.DEFAULT_MASTSEGMENT);
    final CdmaMastSegment mastSegmentNormal = new CdmaMastSegment("zzzMast");

    assertThat(mastSegmentDefault.compareTo(mastSegmentNormal) > 0)
        .withFailMessage("An empty mast segment should be bigger than all other mast segments")
        .isTrue();
  }

  @Test
  public void testPopBatches() {
    final CdmaMastSegment mastSegment = new CdmaMastSegment("200/55");
    for (short i = 0; i < 10; i++) {
      // Each device has a different batch
      mastSegment.addCdmaBatchDevice(i, new CdmaBatchDevice("cd" + i, this.loopbackAddress));
    }

    for (int batchNo = 0; batchNo < 10; batchNo++) {
      assertThat(mastSegment.empty())
          .withFailMessage("There should be at least one CdmaBatch left")
          .isFalse();
      mastSegment.popCdmaBatch();
    }
    assertThat(mastSegment.empty())
        .withFailMessage("All CdmaBatches should have been removed by now")
        .isTrue();
  }
}
