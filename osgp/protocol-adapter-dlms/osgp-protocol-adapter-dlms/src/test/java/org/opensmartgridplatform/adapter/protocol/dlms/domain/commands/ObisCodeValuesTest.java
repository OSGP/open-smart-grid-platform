//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ObisCodeValuesDto;

public class ObisCodeValuesTest {

  @Test
  public void testObisCodeValues() {
    final ObisCodeValuesDto obisCodeValues =
        new ObisCodeValuesDto((byte) 1, (byte) 2, (byte) 3, (byte) 234, (byte) 5, (byte) 255);

    final ObisCode obisCode =
        new ObisCode(
            this.toInt(obisCodeValues.getA()),
            this.toInt(obisCodeValues.getB()),
            this.toInt(obisCodeValues.getC()),
            this.toInt(obisCodeValues.getD()),
            this.toInt(obisCodeValues.getE()),
            this.toInt(obisCodeValues.getF()));

    assertThat(obisCode.bytes()[0]).isEqualTo((byte) 1);
    assertThat(obisCode.bytes()[1]).isEqualTo((byte) 2);
    assertThat(obisCode.bytes()[2]).isEqualTo((byte) 3);
    assertThat(obisCode.bytes()[3]).isEqualTo((byte) 234);
    assertThat(obisCode.bytes()[4]).isEqualTo((byte) 5);
    assertThat(obisCode.bytes()[5]).isEqualTo((byte) 255);
  }

  private int toInt(final byte aByte) {
    return aByte & 0xFF;
  }
}
