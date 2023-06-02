//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClockStatus;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDate;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDateTime;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemTime;

public class CosemDateTimeConverterTest {

  private static final byte FIRST_BYTE_FOR_YEAR = (byte) 0x07;

  private static final byte SECOND_BYTE_FOR_YEAR = (byte) 0xE0;
  private static final byte BYTE_FOR_MONTH = 4;
  private static final byte BYTE_FOR_DAY_OF_MONTH = 7;
  private static final byte BYTE_FOR_DAY_OF_WEEK = (byte) 0xFF;
  private static final byte BYTE_FOR_HOUR_OF_DAY = 10;
  private static final byte BYTE_FOR_MINUTE_OF_HOUR = 34;
  private static final byte BYTE_FOR_SECOND_OF_MINUTE = 35;
  private static final byte BYTE_FOR_HUNDREDS_0F_SECONDS = 10;
  private static final byte FIRST_BYTE_FOR_DEVIATION = -1;
  private static final byte SECOND_BYTE_FOR_DEVIATION = -120;
  private static final byte BYTE_FOR_CLOCKSTATUS = (byte) 0xFF;
  private static final byte[] COSEMDATETIME_BYTE_ARRAY_NORMAL = {
    FIRST_BYTE_FOR_YEAR,
    SECOND_BYTE_FOR_YEAR,
    BYTE_FOR_MONTH,
    BYTE_FOR_DAY_OF_MONTH,
    BYTE_FOR_DAY_OF_WEEK,
    BYTE_FOR_HOUR_OF_DAY,
    BYTE_FOR_MINUTE_OF_HOUR,
    BYTE_FOR_SECOND_OF_MINUTE,
    BYTE_FOR_HUNDREDS_0F_SECONDS,
    FIRST_BYTE_FOR_DEVIATION,
    SECOND_BYTE_FOR_DEVIATION,
    BYTE_FOR_CLOCKSTATUS
  };

  private static final byte FIRST_BYTE_FOR_POSITIVE_DEVIATION = 1;

  private static final byte SECOND_BYTE_FOR_POSITIVE_DEVIATION = 120;
  private static final byte[] COSEMDATETIME_BYTE_ARRAY_POSITIVE_DEVIATION = {
    FIRST_BYTE_FOR_YEAR,
    SECOND_BYTE_FOR_YEAR,
    BYTE_FOR_MONTH,
    BYTE_FOR_DAY_OF_MONTH,
    BYTE_FOR_DAY_OF_WEEK,
    BYTE_FOR_HOUR_OF_DAY,
    BYTE_FOR_MINUTE_OF_HOUR,
    BYTE_FOR_SECOND_OF_MINUTE,
    BYTE_FOR_HUNDREDS_0F_SECONDS,
    FIRST_BYTE_FOR_POSITIVE_DEVIATION,
    SECOND_BYTE_FOR_POSITIVE_DEVIATION,
    BYTE_FOR_CLOCKSTATUS
  };

  private static final byte FIRST_BYTE_FOR_UNSPECIFIED_YEAR = (byte) 0xFF;

  private static final byte SECOND_BYTE_FOR_UNSPECIFIED_YEAR = (byte) 0xFF;
  private static final byte[] COSEMDATETIME_BYTE_ARRAY_UNSPECIFIED_YEAR = {
    FIRST_BYTE_FOR_UNSPECIFIED_YEAR,
    SECOND_BYTE_FOR_UNSPECIFIED_YEAR,
    BYTE_FOR_MONTH,
    BYTE_FOR_DAY_OF_MONTH,
    BYTE_FOR_DAY_OF_WEEK,
    BYTE_FOR_HOUR_OF_DAY,
    BYTE_FOR_MINUTE_OF_HOUR,
    BYTE_FOR_SECOND_OF_MINUTE,
    BYTE_FOR_HUNDREDS_0F_SECONDS,
    FIRST_BYTE_FOR_DEVIATION,
    SECOND_BYTE_FOR_DEVIATION,
    BYTE_FOR_CLOCKSTATUS
  };

  private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

  /**
   * Tests the mapping of the deviation property of a CosemDateTime object for positive and negative
   * values.
   */
  @Test
  public void deviationTest() {

    CosemDateTime cosemDateTime;

    // Test negative
    cosemDateTime =
        this.mapperFactory
            .getMapperFacade()
            .map(COSEMDATETIME_BYTE_ARRAY_NORMAL, CosemDateTime.class);

    assertThat(cosemDateTime).isNotNull();
    assertThat((byte) (cosemDateTime.getDeviation() >> 8)).isEqualTo(FIRST_BYTE_FOR_DEVIATION);
    assertThat((byte) (cosemDateTime.getDeviation() & 0xFF)).isEqualTo(SECOND_BYTE_FOR_DEVIATION);

    // Test positive
    cosemDateTime =
        this.mapperFactory
            .getMapperFacade()
            .map(COSEMDATETIME_BYTE_ARRAY_POSITIVE_DEVIATION, CosemDateTime.class);

    assertThat(cosemDateTime).isNotNull();
    assertThat((byte) (cosemDateTime.getDeviation() >> 8))
        .isEqualTo(FIRST_BYTE_FOR_POSITIVE_DEVIATION);
    assertThat((byte) (cosemDateTime.getDeviation() & 0xFF))
        .isEqualTo(SECOND_BYTE_FOR_POSITIVE_DEVIATION);
  }

  /** Registers the CosemDateTimeConverter to be tested. */
  @BeforeEach
  public void init() {
    this.mapperFactory.getConverterFactory().registerConverter(new CosemDateTimeConverter());
  }

  /** Tests if mapping a byte[] to a CosemDateTime object succeeds. */
  @Test
  public void testToCosemDateTimeMapping() {

    // actual mapping
    final CosemDateTime cosemDateTime =
        this.mapperFactory
            .getMapperFacade()
            .map(COSEMDATETIME_BYTE_ARRAY_NORMAL, CosemDateTime.class);

    // check mapping
    assertThat(cosemDateTime).isNotNull();

    final CosemDate cosemDate = cosemDateTime.getDate();
    assertThat(((byte) (cosemDate.getYear() >> 8))).isEqualTo(FIRST_BYTE_FOR_YEAR);
    assertThat(((byte) (cosemDate.getYear() & 0xFF))).isEqualTo(SECOND_BYTE_FOR_YEAR);
    assertThat(cosemDate.getMonth()).isEqualTo(BYTE_FOR_MONTH);
    assertThat(cosemDate.getDayOfMonth()).isEqualTo(BYTE_FOR_DAY_OF_MONTH);
    assertThat((byte) cosemDate.getDayOfWeek()).isEqualTo(BYTE_FOR_DAY_OF_WEEK);

    final CosemTime cosemTime = cosemDateTime.getTime();
    assertThat(cosemTime.getHour()).isEqualTo(BYTE_FOR_HOUR_OF_DAY);
    assertThat(cosemTime.getMinute()).isEqualTo(BYTE_FOR_MINUTE_OF_HOUR);
    assertThat(cosemTime.getSecond()).isEqualTo(BYTE_FOR_SECOND_OF_MINUTE);
    assertThat(cosemTime.getHundredths()).isEqualTo(BYTE_FOR_HUNDREDS_0F_SECONDS);

    final int deviation = cosemDateTime.getDeviation();
    assertThat(((byte) (deviation >> 8))).isEqualTo(FIRST_BYTE_FOR_DEVIATION);
    assertThat(((byte) (deviation & 0xFF))).isEqualTo(SECOND_BYTE_FOR_DEVIATION);

    assertThat(((byte) ClockStatus.STATUS_NOT_SPECIFIED)).isEqualTo(BYTE_FOR_CLOCKSTATUS);
  }

  /** Tests the mapping of the year property of a CosemDateTime object when it is unspecified. */
  @Test
  public void yearTest() {

    final CosemDateTime cosemDateTime =
        this.mapperFactory
            .getMapperFacade()
            .map(COSEMDATETIME_BYTE_ARRAY_UNSPECIFIED_YEAR, CosemDateTime.class);

    assertThat(cosemDateTime).isNotNull();
    assertThat(cosemDateTime.getDate()).isNotNull();
    assertThat(cosemDateTime.getDate().getYear()).isEqualTo(CosemDate.YEAR_NOT_SPECIFIED);
  }
}
