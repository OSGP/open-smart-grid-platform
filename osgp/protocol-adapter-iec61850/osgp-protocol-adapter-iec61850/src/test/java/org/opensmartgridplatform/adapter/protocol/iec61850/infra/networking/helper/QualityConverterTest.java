/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class QualityConverterTest {

  @Test
  public void testToShort() throws Exception {

    // arrange
    final byte[] ba = new byte[2];
    ba[0] = (byte) 193;
    ba[1] = (byte) 0;

    // act
    final short s = QualityConverter.toShort(ba);

    // assert
    assertThat(s).isEqualTo((short) 131);
  }
}
