// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.ObisCodeValues;

public class ObisCodeValuesTest {

  @Test
  public void testObisCodeValues() {
    final AdhocMapper mapper = new AdhocMapper();

    final ObisCodeValues obisCodeValues = new ObisCodeValues();
    obisCodeValues.setA((short) 1);
    obisCodeValues.setB((short) 2);
    obisCodeValues.setC((short) 3);
    obisCodeValues.setD((short) 234);
    obisCodeValues.setE((short) 5);
    obisCodeValues.setF((short) 255);

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues
        obisCodeValues2 =
            mapper.map(
                obisCodeValues,
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues
                    .class);

    assertThat(obisCodeValues2.getA()).isEqualTo((byte) 1);
    assertThat(obisCodeValues2.getB()).isEqualTo((byte) 2);
    assertThat(obisCodeValues2.getC()).isEqualTo((byte) 3);
    assertThat(obisCodeValues2.getD()).isEqualTo((byte) -22);
    assertThat(obisCodeValues2.getE()).isEqualTo((byte) 5);
    assertThat(obisCodeValues2.getF()).isEqualTo((byte) -1);
  }

  @Test
  public void testObisCodeValues2() {
    final AdhocMapper mapper = new AdhocMapper();

    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues
        obisCodeValues1 =
            new org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ObisCodeValues(
                (byte) 1, (byte) 2, (byte) 3, (byte) -22, (byte) 5, (byte) -1);

    final ObisCodeValues obisCodeValues2 = mapper.map(obisCodeValues1, ObisCodeValues.class);

    assertThat(obisCodeValues2.getA()).isEqualTo((short) 1);
    assertThat(obisCodeValues2.getB()).isEqualTo((short) 2);
    assertThat(obisCodeValues2.getC()).isEqualTo((short) 3);
    assertThat(obisCodeValues2.getD()).isEqualTo((short) 234);
    assertThat(obisCodeValues2.getE()).isEqualTo((short) 5);
    assertThat(obisCodeValues2.getF()).isEqualTo((short) 255);
  }
}
