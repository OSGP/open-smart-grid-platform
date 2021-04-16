/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
