package org.opensmartgridplatform.shared.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    final String formattedJoda = new DateTime(date).toString(dateTimeFormat);
    final String formattedJava = JavaTimeHelpers.formatDate(date, dateTimeFormat);

    assertThat(formattedJoda).isEqualTo(formattedJava);
  }

  @Test
  void shouldFormatLocalTimesTheSameAsJoda() {
    final String timeToFormat = "10:15:45";
    final String formatterString = "HHmmss";

    final String jodaFormatted =
        org.joda.time.LocalTime.parse(timeToFormat).toString(formatterString);
    final String formattedJava =
        JavaTimeHelpers.formatTemporal(LocalTime.parse(timeToFormat), formatterString);

    assertThat(jodaFormatted).isEqualTo(formattedJava);
  }

  @Test
  void shouldMapGregorianCalendarTheSameAsJoda() {
    final ZonedDateTime zonedDateTime =
        ZonedDateTime.of(1998, 1, 24, 1, 1, 1, 123999999, ZoneId.systemDefault());
    final GregorianCalendar gregorianCalendar = GregorianCalendar.from(zonedDateTime);

    final DateTime joda = new DateTime(gregorianCalendar).toDateTime(DateTimeZone.UTC);
    final ZonedDateTime javaApi =
        JavaTimeHelpers.gregorianCalendarToZonedDateTime(gregorianCalendar, ZoneId.of("UTC"));

    assertThat(javaApi.getYear()).isEqualTo(joda.getYear());
    assertThat(javaApi.getMonthValue()).isEqualTo(joda.getMonthOfYear());
    assertThat(javaApi.getDayOfMonth()).isEqualTo(joda.getDayOfMonth());
    assertThat(javaApi.getHour()).isEqualTo(joda.getHourOfDay());
    assertThat(javaApi.getMinute()).isEqualTo(joda.getMinuteOfHour());
    assertThat(javaApi.getSecond()).isEqualTo(joda.getSecondOfMinute());
    assertThat(JavaTimeHelpers.getMillisFrom(javaApi)).isEqualTo(joda.getMillisOfSecond());
    // Checks if the timezone offset in seconds is the same for joda and java api
    assertThat(javaApi.getZone().getRules().getOffset(Instant.now()).getTotalSeconds())
        .isEqualTo(joda.getZone().getOffset(new DateTime().getMillis() / MILLIS_TO_SECONDS));
  }

  @Test
  void shouldMakeZonedDateTimeAtStartOfDay() {
    final Date now = Date.from(ZonedDateTime.now(ZoneId.of("UTC")).toInstant());
    final DateTime jodaTime = new DateTime(now).withTimeAtStartOfDay();
    final ZonedDateTime javaTime =
        JavaTimeHelpers.getZonedDateTimeWithStartAtBeginOfDay(
            LocalDate.ofInstant(now.toInstant(), ZoneId.of("UTC")), ZoneId.of("UTC"));

    assertThat(javaTime.getYear()).isEqualTo(jodaTime.getYear());
    assertThat(javaTime.getMonthValue()).isEqualTo(jodaTime.getMonthOfYear());
    assertThat(javaTime.getDayOfMonth()).isEqualTo(jodaTime.getDayOfMonth());
    assertThat(javaTime.getHour()).isEqualTo(jodaTime.getHourOfDay());
    assertThat(javaTime.getMinute()).isEqualTo(jodaTime.getMinuteOfHour());
    assertThat(javaTime.getSecond()).isEqualTo(jodaTime.getSecondOfMinute());
    assertThat(JavaTimeHelpers.getMillisFrom(javaTime)).isEqualTo(jodaTime.getMillisOfSecond());
  }
}
