// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
