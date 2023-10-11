// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.publiclighting.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.net.InetAddress;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaBatch;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaBatchDevice;

public class CdmaBatchTest {

  private final String loopbackAddress = InetAddress.getLoopbackAddress().getHostAddress();

  @Test
  public void newBatchNumberNull() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> {
              new CdmaBatch(null);
            });
  }

  @Test
  public void newCdmaBatch() {
    final CdmaBatch batch = new CdmaBatch((short) 1);

    final CdmaBatchDevice cd1 = new CdmaBatchDevice("cd1", this.loopbackAddress);
    batch.addCdmaBatchDevice(cd1);

    final CdmaBatchDevice cd2 = new CdmaBatchDevice("cd2", this.loopbackAddress);
    batch.addCdmaBatchDevice(cd2);

    assertThat(batch.getBatchNumber()).isEqualTo(Short.valueOf((short) 1));
    assertThat(batch.getCdmaBatchDevices().size())
        .withFailMessage("Batch should contain 2 devices")
        .isEqualTo(2);
  }

  @Test
  public void equalsWhenBatchNumbersMatch() {
    final CdmaBatch batch1 = new CdmaBatch((short) 7);
    final CdmaBatch batch2 = new CdmaBatch((short) 7);

    assertThat(batch1)
        .withFailMessage("Batches with the same batch number should be equal")
        .isEqualTo(batch2);
  }

  @Test
  public void largerWhenBatchNumberLarger() {
    final CdmaBatch batch3 = new CdmaBatch((short) 3);
    final CdmaBatch batch2 = new CdmaBatch((short) 2);
    assertThat(batch3)
        .withFailMessage("Batches with different batch numbers should not be equal")
        .isNotEqualTo(batch2);
    assertThat(batch3.compareTo(batch2) > 0).isTrue();
  }
}
