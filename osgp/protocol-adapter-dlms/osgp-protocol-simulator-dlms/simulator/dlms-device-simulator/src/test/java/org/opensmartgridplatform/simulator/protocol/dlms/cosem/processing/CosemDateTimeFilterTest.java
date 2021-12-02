/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;
import org.openmuc.jdlms.datatypes.CosemDate;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.CosemDateTime.ClockStatus;
import org.openmuc.jdlms.datatypes.CosemTime;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.simulator.protocol.dlms.cosem.CaptureObject;

public class CosemDateTimeFilterTest {

  @Test
  public void testMatchBefore() {
    final CosemDateTimeFilter filter = new CosemDateTimeFilter(this.createRangeDescriptor());

    final Calendar cal = Calendar.getInstance();
    cal.set(2016, Calendar.DECEMBER, 31);

    assertThat(filter.match(cal)).isFalse();
  }

  @Test
  public void testMatchInBetween() {
    final CosemDateTimeFilter filter = new CosemDateTimeFilter(this.createRangeDescriptor());

    final Calendar cal = Calendar.getInstance();

    // month in calendar is 0-based, month in joda DateTime is 1-based
    cal.set(2017, Calendar.JANUARY, 1, 12, 0);

    assertThat(filter.match(cal)).isTrue();
  }

  @Test
  public void testMatchAfter() {
    final CosemDateTimeFilter filter = new CosemDateTimeFilter(this.createRangeDescriptor());

    final Calendar cal = Calendar.getInstance();

    // month in calendar is 0-based, month in joda DateTime is 1-based
    cal.set(2017, Calendar.JANUARY, 2);

    assertThat(filter.match(cal)).isFalse();
  }

  private List<DataObject> createRangeDescriptor() {
    final DataObject captureObject =
        new CaptureObject(8, "0.0.1.0.0.255", (byte) 2, 0).asDataObject();
    final DataObject dateTimeFrom = this.asDataObject(new DateTime(2017, 1, 1, 0, 0), 0, false);
    final DataObject dateTimeTo = this.asDataObject(new DateTime(2017, 1, 2, 0, 0), 0, false);
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
