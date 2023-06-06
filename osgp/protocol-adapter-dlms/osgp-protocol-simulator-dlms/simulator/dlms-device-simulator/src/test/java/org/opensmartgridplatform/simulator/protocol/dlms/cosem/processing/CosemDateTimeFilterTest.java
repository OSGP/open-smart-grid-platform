// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openmuc.jdlms.datatypes.CosemDate;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.CosemDateTime.ClockStatus;
import org.openmuc.jdlms.datatypes.CosemTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.CaptureObject;

class CosemDateTimeFilterTest {

  @ParameterizedTest
  @CsvSource({
    "2018,12,31,23,0,0,0,2018-12-31T23:00:00Z", // winter
    "2018,06,31,23,0,0,0,2018-07-01T23:00:00Z,", // summer
    "2018,06,31,23,0,0,-60,2018-07-01T22:00:00Z" // deviation
  })
  void checkCorrectDeviation(
      final int year,
      final int month,
      final int dayOfMonth,
      final int hour,
      final int minute,
      final int second,
      final int deviation,
      final String expectedDateTime) {
    final CosemDateTimeFilter filter = new CosemDateTimeFilter(this.createRangeDescriptor());

    final CosemDateTime cosemDateTime =
        new CosemDateTime(
            year,
            month,
            dayOfMonth,
            0xff,
            hour,
            minute,
            second,
            0,
            deviation,
            new CosemDateTime.ClockStatus[0]);

    Assertions.assertThat(filter.toCalendar(cosemDateTime).toInstant())
        .hasToString(expectedDateTime);
  }

  @Test
  void testMatchBefore() {
    final CosemDateTimeFilter filter = new CosemDateTimeFilter(this.createRangeDescriptor());

    final Calendar cal = Calendar.getInstance();
    cal.set(2016, Calendar.DECEMBER, 31);

    assertThat(filter.match(cal)).isFalse();
  }

  @Test
  void testMatchInBetween() {
    final CosemDateTimeFilter filter = new CosemDateTimeFilter(this.createRangeDescriptor());

    final Calendar cal = Calendar.getInstance();

    // month in calendar is 0-based, month in joda DateTime is 1-based
    cal.set(2017, Calendar.JANUARY, 1, 12, 0);

    assertThat(filter.match(cal)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 0, 1})
  void testFromAmsterdamTime(final int offsetSecond) {
    final CosemDateTimeFilter filter = new CosemDateTimeFilter(this.createRangeDescriptor());

    final Calendar cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getTimeZone("Europe/Amsterdam"));

    // month in calendar is 0-based, month in joda DateTime is 1-based
    cal.set(2017, Calendar.JANUARY, 1, 1, 0, 0);
    cal.set(Calendar.MILLISECOND, 0);

    cal.add(Calendar.SECOND, offsetSecond);
    assertThat(filter.match(cal)).isEqualTo(offsetSecond > 0);
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 0, 1})
  void testToAmsterdamTime(final int offsetSecond) {
    final CosemDateTimeFilter filter = new CosemDateTimeFilter(this.createRangeDescriptor());

    final Calendar cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getTimeZone("Europe/Amsterdam"));

    // month in calendar is 0-based, month in joda DateTime is 1-based
    cal.set(2017, Calendar.JANUARY, 2, 1, 0, 0);
    cal.set(Calendar.MILLISECOND, 0);

    cal.add(Calendar.SECOND, offsetSecond);

    assertThat(filter.match(cal)).isEqualTo(offsetSecond < 0);
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 0, 1})
  void testFrom(final int offsetSecond) {
    final CosemDateTimeFilter filter = new CosemDateTimeFilter(this.createRangeDescriptor());

    final Calendar cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getTimeZone("UTC"));

    // month in calendar is 0-based, month in joda DateTime is 1-based
    cal.set(2017, Calendar.JANUARY, 1, 0, 0, 0);
    cal.set(Calendar.MILLISECOND, 0);

    cal.add(Calendar.SECOND, offsetSecond);
    assertThat(filter.match(cal)).isEqualTo(offsetSecond > 0);
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 0, 1})
  void testTo(final int offsetSecond) {
    final CosemDateTimeFilter filter = new CosemDateTimeFilter(this.createRangeDescriptor());

    final Calendar cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getTimeZone("UTC"));

    // month in calendar is 0-based, month in joda DateTime is 1-based
    cal.set(2017, Calendar.JANUARY, 2, 0, 0, 0);
    cal.set(Calendar.MILLISECOND, 0);

    cal.add(Calendar.SECOND, offsetSecond);

    assertThat(filter.match(cal)).isEqualTo(offsetSecond < 0);
  }

  private List<DataObject> createRangeDescriptor() {
    final DataObject captureObject =
        new CaptureObject(8, "0.0.1.0.0.255", (byte) 2, 0).asDataObject();
    final DataObject dateTimeFrom =
        this.asDataObject(new DateTime(2017, 1, 1, 0, 0, DateTimeZone.UTC), 0, false);
    final DataObject dateTimeTo =
        this.asDataObject(new DateTime(2017, 1, 2, 0, 0, DateTimeZone.UTC), 0, false);
    return Arrays.asList(captureObject, dateTimeFrom, dateTimeTo);
  }

  /**
   * Creates a COSEM date-time object based on the given {@code dateTime}. This COSEM date-time will
   * be for the same instant in time as the given {@code dateTime} but may be for another time zone.
   *
   * <p>Because the time zone with the {@code deviation} may be different than the one with the
   * {@code dateTime}, and the {@code deviation} alone does not provide sufficient information on
   * whether daylight savings is active for the given instant in time, {@code dst} has to be
   * provided to indicate whether daylight savings are active.
   *
   * @param dateTime a DateTime indicating an instant in time to be used for the COSEM date-time.
   * @param deviation the deviation in minutes of local time to GMT to be included in the COSEM
   *     date-time.
   * @param dst {@code true} if daylight savings are active for the instant of the COSEM date-time,
   *     otherwise {@code false}.
   * @return a DataObject having a CosemDateTime for the instant of the given DateTime, with the
   *     given deviation and DST status information, as value.
   */
  public DataObject asDataObject(final DateTime dateTime, final int deviation, final boolean dst) {
    /*
     * Create a date time that may not point to the right instant in time,
     * but that will give proper values getting the different fields for the
     * COSEM date and time objects.
     */
    final DateTime dateTimeWithOffset =
        dateTime.toDateTime(DateTimeZone.UTC).minusMinutes(deviation);
    final CosemDate cosemDate =
        new CosemDate(
            dateTimeWithOffset.getYear(),
            dateTimeWithOffset.getMonthOfYear(),
            dateTimeWithOffset.getDayOfMonth());
    final CosemTime cosemTime =
        new CosemTime(
            dateTimeWithOffset.getHourOfDay(),
            dateTimeWithOffset.getMinuteOfHour(),
            dateTimeWithOffset.getSecondOfMinute(),
            dateTimeWithOffset.getMillisOfSecond() / 10);
    final ClockStatus[] clockStatusBits;

    if (dst) {
      clockStatusBits = new ClockStatus[1];
      clockStatusBits[0] = ClockStatus.DAYLIGHT_SAVING_ACTIVE;
    } else {
      clockStatusBits = new ClockStatus[0];
    }
    final CosemDateTime cosemDateTime =
        new CosemDateTime(cosemDate, cosemTime, deviation, clockStatusBits);
    return DataObject.newOctetStringData(cosemDateTime.encode());
  }
}
