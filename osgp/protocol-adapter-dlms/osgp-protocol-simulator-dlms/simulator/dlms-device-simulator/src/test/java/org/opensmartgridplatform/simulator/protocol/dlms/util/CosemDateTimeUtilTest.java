/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.simulator.protocol.dlms.util;

import java.util.Calendar;
import java.util.TimeZone;
import org.assertj.core.api.Assertions;
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

    Assertions.assertThat(CosemDateTimeUtil.calculateDeviation(calendar))
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

    Assertions.assertThat(cosemDateTime.get(Field.YEAR)).isEqualTo(year);
    Assertions.assertThat(cosemDateTime.get(Field.MONTH)).isEqualTo(monthIndex);
    Assertions.assertThat(cosemDateTime.get(Field.DAY_OF_MONTH)).isEqualTo(dayOfMonth);
    Assertions.assertThat(cosemDateTime.get(Field.HOUR)).isEqualTo(hour);
    Assertions.assertThat(cosemDateTime.get(Field.MINUTE)).isEqualTo(minute);
    Assertions.assertThat(cosemDateTime.get(Field.SECOND)).isEqualTo(seconds);
    Assertions.assertThat(cosemDateTime.get(Field.HUNDREDTHS)).isZero();
    Assertions.assertThat(cosemDateTime.get(Field.DEVIATION)).isEqualTo(expectedDeviation);
  }
}
