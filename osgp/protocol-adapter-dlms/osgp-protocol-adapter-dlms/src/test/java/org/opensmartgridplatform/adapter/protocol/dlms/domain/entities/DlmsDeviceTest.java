// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class DlmsDeviceTest {
  @Test
  public void returnsIfDeviceNeedsInvocationCounter() {
    assertThat(
            new DlmsDeviceBuilder()
                .withHls5Active(true)
                .withProtocol("SMR")
                .build()
                .needsInvocationCounter())
        .isTrue();
    assertThat(
            new DlmsDeviceBuilder()
                .withHls5Active(false)
                .withProtocol("SMR")
                .build()
                .needsInvocationCounter())
        .isFalse();
    assertThat(
            new DlmsDeviceBuilder()
                .withHls5Active(true)
                .withProtocol("DSMR")
                .build()
                .needsInvocationCounter())
        .isFalse();
  }
}
