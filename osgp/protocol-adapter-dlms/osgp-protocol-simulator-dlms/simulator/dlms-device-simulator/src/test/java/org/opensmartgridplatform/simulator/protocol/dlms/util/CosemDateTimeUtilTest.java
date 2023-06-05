// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.TimeZone;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openmuc.jdlms.datatypes.CosemDateFormat.Field;
import org.openmuc.jdlms.datatypes.CosemDateTime;

class CosemDateTimeUtilTest {

  @ParameterizedTest
  @CsvSource({"winter time,2022,11,6,15,0,0,-60", "summer time,2022,10,6,15,0,0,-120"})
  void calculateDeviation(
      final String testDescription,
      final int year,
      final int month,
      final int dayOfMonth,
      final int hour,
      final int minute,
      final int seconds,
      final int expectedDeviation) {

    final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Amsterdam"));
    calendar.set(year, month - 1, dayOfMonth, hour, minute, seconds);

    assertThat(CosemDateTimeUtil.calculateDeviation(calendar))
        .as(testDescription)
        .isEqualTo(expectedDeviation);
  }

  @ParameterizedTest
  @CsvSource({
    "Europe/Amsterdam,2022,11,6,15,0,0,-60",
    "Europe/Amsterdam,2022,10,6,15,0,0,-120",
    "UTC,2022,11,6,15,0,0,0",
    "UTC,2022,10,6,15,0,0,0"
  })
  void toCosemDateTime(
      final String timezone,
      final int year,
      final int monthIndex,
      final int dayOfMonth,
      final int hour,
      final int minute,
      final int seconds,
      final int expectedDeviation) {

    final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));
    calendar.set(year, monthIndex - 1, dayOfMonth, hour, minute, seconds);

    final CosemDateTime cosemDateTime = CosemDateTimeUtil.toCosemDateTime(calendar);

    assertThat(cosemDateTime.get(Field.YEAR)).isEqualTo(year);
    assertThat(cosemDateTime.get(Field.MONTH)).isEqualTo(monthIndex);
    assertThat(cosemDateTime.get(Field.DAY_OF_MONTH)).isEqualTo(dayOfMonth);
    assertThat(cosemDateTime.get(Field.HOUR)).isEqualTo(hour);
    assertThat(cosemDateTime.get(Field.MINUTE)).isEqualTo(minute);
    assertThat(cosemDateTime.get(Field.SECOND)).isEqualTo(seconds);
    assertThat(cosemDateTime.get(Field.HUNDREDTHS)).isZero();
    assertThat(cosemDateTime.get(Field.DEVIATION)).isEqualTo(expectedDeviation);
  }
}
