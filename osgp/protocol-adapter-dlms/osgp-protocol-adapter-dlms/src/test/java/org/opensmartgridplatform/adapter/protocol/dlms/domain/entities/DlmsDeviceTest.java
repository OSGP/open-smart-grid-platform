// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DlmsDeviceTest {

  @Test
  void isAbleToSetAObjectConfigLookupType() {
    assertThat(
            new DlmsDeviceBuilder()
                .withHls5Active(true)
                .withProtocol("SMR")
                .withConfigLookupType("DUMMY")
                .build()
                .getConfigLookupType())
        .isEqualTo("DUMMY");
  }

  @Test
  void isAbleToUnSetAObjectConfigLookupType() {
    assertThat(
            new DlmsDeviceBuilder()
                .withHls5Active(true)
                .withProtocol("SMR")
                .withConfigLookupType(null)
                .build()
                .getConfigLookupType())
        .isNull();
  }

  @Test
  void returnsIfDeviceNeedsInvocationCounter() {
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
