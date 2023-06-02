//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDate;

public class CosemDateConverterTest {

  private static final byte FIRST_BYTE_FOR_YEAR = (byte) 0x07;

  private static final byte SECOND_BYTE_FOR_YEAR = (byte) 0xE0;
  private static final byte BYTE_FOR_MONTH = 4;
  private static final byte BYTE_FOR_DAY_OF_MONTH = 7;
  private static final byte BYTE_FOR_DAY_OF_WEEK = (byte) 0xFF;
  private static final byte[] COSEMDATE_BYTE_ARRAY = {
    FIRST_BYTE_FOR_YEAR,
    SECOND_BYTE_FOR_YEAR,
    BYTE_FOR_MONTH,
    BYTE_FOR_DAY_OF_MONTH,
    BYTE_FOR_DAY_OF_WEEK
  };
  private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

  /** Tests if mapping a byte[] to a CosemDate object succeeds. */
  @Test
  public void testToCosemDateMapping() {

    // register the CosemDateConverter, since the test converts to a
    // CosemDate object.
    this.mapperFactory.getConverterFactory().registerConverter(new CosemDateConverter());

    // actual mapping
    final CosemDate cosemDate =
        this.mapperFactory.getMapperFacade().map(COSEMDATE_BYTE_ARRAY, CosemDate.class);

    // check mapping
    assertThat(cosemDate).isNotNull();
    assertThat(((byte) (cosemDate.getYear() >> 8))).isEqualTo(FIRST_BYTE_FOR_YEAR);
    assertThat(((byte) (cosemDate.getYear() & 0xFF))).isEqualTo(SECOND_BYTE_FOR_YEAR);
    assertThat(cosemDate.getMonth()).isEqualTo(BYTE_FOR_MONTH);
    assertThat(cosemDate.getDayOfMonth()).isEqualTo(BYTE_FOR_DAY_OF_MONTH);
    assertThat((byte) cosemDate.getDayOfWeek()).isEqualTo(BYTE_FOR_DAY_OF_WEEK);
  }

  /** Tests the mapping of wildcards. */
  @Test
  public void testWildCards() {
    byte[] source = new byte[] {(byte) 0x07, (byte) 0xDF, 6, (byte) 0xFF, (byte) 0xFF};
    final CosemDateConverter converter = new CosemDateConverter();
    CosemDate date = converter.convert(source, converter.getBType(), null);
    assertThat(date.getYear()).isEqualTo(2015);
    assertThat(date.getMonth()).isEqualTo(6);
    assertThat(date.getDayOfMonth()).isEqualTo(0xFF);
    assertThat(date.getDayOfWeek()).isEqualTo(0xFF);

    source = new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFE, (byte) 0xFD, (byte) 0xFF};
    date = converter.convert(source, converter.getBType(), null);
    assertThat(date.getYear()).isEqualTo(0xFFFF);
    assertThat(date.getMonth()).isEqualTo(0xFE);
    assertThat(date.getDayOfMonth()).isEqualTo(0xFD);
    assertThat(date.getDayOfWeek()).isEqualTo(0xFF);

    source = new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFE, 21, 3};
    date = converter.convert(source, converter.getBType(), null);
    assertThat(date.getYear()).isEqualTo(0xFFFF);
    assertThat(date.getMonth()).isEqualTo(0xFE);
    assertThat(date.getDayOfMonth()).isEqualTo(21);
    assertThat(date.getDayOfWeek()).isEqualTo(3);
  }
}
