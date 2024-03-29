package org.opensmartgridplatform.shared.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

class JavaTimeHelpersTest {

  public static final int MILLIS_TO_SECONDS = 1000;

  @Test
  void shouldCropNanoToMillis() {
    final ZonedDateTime dateWithNano =
        ZonedDateTime.of(1998, 1, 24, 1, 1, 1, 123999999, ZoneId.systemDefault());
    final long croppedMillis = JavaTimeHelpers.getMillisFrom(dateWithNano);
    assertThat(croppedMillis).isEqualTo(123);
  }

  @Test
  void shouldFormatDatesTheSameAsJoda() {
    final Instant instant = Instant.ofEpochMilli(1000L);
    final Date date = Date.from(instant);

    final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);
    final String formattedJoda = new DateTime(date).toString(dateTimeFormat);
    final String formattedJava = JavaTimeHelpers.formatDate(date, formatter);

    assertThat(formattedJoda).isEqualTo(formattedJava);
  }

  @Test
  void shouldMapGregorianCalendarTheSameAsJoda() {
    final ZonedDateTime zonedDateTime =
        ZonedDateTime.of(1998, 1, 24, 1, 1, 1, 123999999, ZoneId.systemDefault());
    final GregorianCalendar gregorianCalendar = GregorianCalendar.from(zonedDateTime);

    final DateTime joda = new DateTime(gregorianCalendar).toDateTime(DateTimeZone.UTC);
    final ZonedDateTime javaApi =
        JavaTimeHelpers.gregorianCalendarToZonedDateTime(gregorianCalendar, ZoneId.of("UTC"));

    this.validateJodaDateToJavaDate(javaApi, joda);
  }

  @Test
  void shouldDetermineDayLightSavingsCorrectly() {
    final ZonedDateTime daylightSavingDate =
        ZonedDateTime.of(2023, 3, 26, 2, 0, 0, 0, ZoneId.of("Europe/Amsterdam"));

    final ZonedDateTime notDaylightSavingDate =
        ZonedDateTime.of(2023, 3, 25, 2, 0, 0, 0, ZoneId.of("Europe/Amsterdam"));

    assertThat(JavaTimeHelpers.isDayLightSavingsActive(daylightSavingDate)).isTrue();
    assertThat(JavaTimeHelpers.isDayLightSavingsActive(notDaylightSavingDate)).isFalse();
  }

  @Test
  void shouldReturnSameOffsetAsJoda() {
    final Instant instant = Instant.ofEpochMilli(1000L);
    final Date date = Date.from(instant);

    final DateTime joda = new DateTime(date);
    final ZonedDateTime java = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());

    final int jodaOffset = joda.getZone().getOffset(joda.getMillis());
    final int javaOffset = JavaTimeHelpers.getOffsetForZonedDateTimeInMillis(java);

    assertThat(jodaOffset).isEqualTo(javaOffset);
  }

  @Test
  void shouldParseDatesLikeJoda() {
    final String localDateString = "1998-01-24";
    final String localDateTimeString = "1998-01-24T13:00:00";
    final String zonedDateTimeString = "2015-03-29T02:00:00.000+01:00";

    final ZonedDateTime localDateParsedJava = JavaTimeHelpers.parseToZonedDateTime(localDateString);
    final ZonedDateTime localDateTimeParsedJava =
        JavaTimeHelpers.parseToZonedDateTime(localDateTimeString);
    final ZonedDateTime zonedDateTimeParsedJava =
        JavaTimeHelpers.parseToZonedDateTime(zonedDateTimeString);

    final DateTime localDateParsedJoda = DateTime.parse(localDateString);
    final DateTime localDateTimeParsedJoda = DateTime.parse(localDateTimeString);
    final DateTime zonedDateTimeParsedJoda = DateTime.parse(zonedDateTimeString);

    this.validateJodaDateToJavaDate(localDateParsedJava, localDateParsedJoda);
    this.validateJodaDateToJavaDate(localDateTimeParsedJava, localDateTimeParsedJoda);
    this.validateJodaDateToJavaDate(zonedDateTimeParsedJava, zonedDateTimeParsedJoda);
  }

  private void validateJodaDateToJavaDate(final ZonedDateTime javaApi, final DateTime joda) {
    assertThat(javaApi.getYear()).isEqualTo(joda.getYear());
    assertThat(javaApi.getMonthValue()).isEqualTo(joda.getMonthOfYear());
    assertThat(javaApi.getDayOfMonth()).isEqualTo(joda.getDayOfMonth());
    assertThat(javaApi.getHour()).isEqualTo(joda.getHourOfDay());
    assertThat(javaApi.getMinute()).isEqualTo(joda.getMinuteOfHour());
    assertThat(javaApi.getSecond()).isEqualTo(joda.getSecondOfMinute());
    assertThat(JavaTimeHelpers.getMillisFrom(javaApi)).isEqualTo(joda.getMillisOfSecond());
    // Checks if the timezone offset in seconds is the same for joda and java api
    assertThat(javaApi.getZone().getRules().getOffset(Instant.now()).getTotalSeconds())
        .isEqualTo(joda.getZone().getOffset(new DateTime().getMillis()) / MILLIS_TO_SECONDS);
  }
}
