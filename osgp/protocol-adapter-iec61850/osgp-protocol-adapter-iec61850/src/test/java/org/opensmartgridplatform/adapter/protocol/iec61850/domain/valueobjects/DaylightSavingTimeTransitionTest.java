// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

public class DaylightSavingTimeTransitionTest {
  public static final DateTimeZone amsterdamDTZ = DateTimeZone.forID("Europe/Amsterdam");

  public static final String DST_START_MWD_AMSTERDAM = "M3.5.0/2";
  public static final String DST_END_MWD_AMSTERDAM = "M10.5.0/3";

  public static final String DST_START_J_COUNTING_FEB29_AMSTERDAM_2015 = "87/2";
  public static final String DST_END_J_COUNTING_FEB29_AMSTERDAM_2015 = "297/3";

  public static final String DST_START_J_IGNORING_FEB29_AMSTERDAM_2015 = "J88/2";
  public static final String DST_END_J_IGNORING_FEB29_AMSTERDAM_2015 = "J298/3";

  public static final String DST_START_J_COUNTING_FEB29_AMSTERDAM_2016 = "86/2";
  public static final String DST_END_J_COUNTING_FEB29_AMSTERDAM_2016 = "303/3";

  public static final String DST_START_J_IGNORING_FEB29_AMSTERDAM_2016 = "J86/2";
  public static final String DST_END_J_IGNORING_FEB29_AMSTERDAM_2016 = "J303/3";

  public static final DateTime DST_START_DATE_TIME_AMSTERDAM_2015 =
      DateTime.parse("2015-03-29T02:00:00.000+01:00");
  public static final DateTime DST_END_DATE_TIME_AMSTERDAM_2015 =
      DateTime.parse("2015-10-25T03:00:00.000+02:00");
  public static final DateTime DST_START_DATE_TIME_AMSTERDAM_2016 =
      DateTime.parse("2016-03-27T02:00:00.000+01:00");
  public static final DateTime DST_END_DATE_TIME_AMSTERDAM_2016 =
      DateTime.parse("2016-10-30T03:00:00.000+02:00");

  @Test
  public void testDaylightSavingTimeStartAmsterdam2015DayOfWeekOfMonth() {
    final DaylightSavingTimeTransition dstTransition =
        new DaylightSavingTimeTransition(DST_START_MWD_AMSTERDAM);
    assertThat(dstTransition.getDateTimeForYear(2015).toDateTime(DateTimeZone.UTC))
        .isEqualTo(DST_START_DATE_TIME_AMSTERDAM_2015.toDateTime(DateTimeZone.UTC));

    assertThat(
            DaylightSavingTimeTransition.forDateTimeAccordingToFormat(
                    DST_START_DATE_TIME_AMSTERDAM_2015,
                    DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH)
                .getTransition())
        .isEqualTo(DST_START_MWD_AMSTERDAM);
  }

  @Test
  public void testDaylightSavingTimeEndAmsterdam2015DayOfWeekOfMonth() {
    final DaylightSavingTimeTransition dstTransition =
        new DaylightSavingTimeTransition(DST_END_MWD_AMSTERDAM);
    assertThat(dstTransition.getDateTimeForYear(2015).toDateTime(DateTimeZone.UTC))
        .isEqualTo(DST_END_DATE_TIME_AMSTERDAM_2015.toDateTime(DateTimeZone.UTC));

    assertThat(
            DaylightSavingTimeTransition.forDateTimeAccordingToFormat(
                    DST_END_DATE_TIME_AMSTERDAM_2015,
                    DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH)
                .getTransition())
        .isEqualTo(DST_END_MWD_AMSTERDAM);
  }

  @Test
  public void testDaylightSavingTimeStartAmsterdam2015JulianCountingFeb29() {
    final DaylightSavingTimeTransition dstTransition =
        new DaylightSavingTimeTransition(DST_START_J_COUNTING_FEB29_AMSTERDAM_2015);
    assertThat(dstTransition.getDateTimeForYear(2015).toDateTime(DateTimeZone.UTC))
        .isEqualTo(DST_START_DATE_TIME_AMSTERDAM_2015.toDateTime(DateTimeZone.UTC));

    assertThat(
            DaylightSavingTimeTransition.forDateTimeAccordingToFormat(
                    DST_START_DATE_TIME_AMSTERDAM_2015,
                    DaylightSavingTimeTransition.DstTransitionFormat
                        .JULIAN_DAY_COUNTING_FEBRUARY_29)
                .getTransition())
        .isEqualTo(DST_START_J_COUNTING_FEB29_AMSTERDAM_2015);
  }

  @Test
  public void testDaylightSavingTimeEndAmsterdam2015JulianCountingFeb29() {
    final DaylightSavingTimeTransition dstTransition =
        new DaylightSavingTimeTransition(DST_END_J_COUNTING_FEB29_AMSTERDAM_2015);
    assertThat(dstTransition.getDateTimeForYear(2015).toDateTime(DateTimeZone.UTC))
        .isEqualTo(DST_END_DATE_TIME_AMSTERDAM_2015.toDateTime(DateTimeZone.UTC));

    assertThat(
            DaylightSavingTimeTransition.forDateTimeAccordingToFormat(
                    DST_END_DATE_TIME_AMSTERDAM_2015,
                    DaylightSavingTimeTransition.DstTransitionFormat
                        .JULIAN_DAY_COUNTING_FEBRUARY_29)
                .getTransition())
        .isEqualTo(DST_END_J_COUNTING_FEB29_AMSTERDAM_2015);
  }

  @Test
  public void testDaylightSavingTimeStartAmsterdam2015JulianIgnoringFeb29() {
    final DaylightSavingTimeTransition dstTransition =
        new DaylightSavingTimeTransition(DST_START_J_IGNORING_FEB29_AMSTERDAM_2015);
    assertThat(dstTransition.getDateTimeForYear(2015).toDateTime(DateTimeZone.UTC))
        .isEqualTo(DST_START_DATE_TIME_AMSTERDAM_2015.toDateTime(DateTimeZone.UTC));

    assertThat(
            DaylightSavingTimeTransition.forDateTimeAccordingToFormat(
                    DST_START_DATE_TIME_AMSTERDAM_2015,
                    DaylightSavingTimeTransition.DstTransitionFormat
                        .JULIAN_DAY_IGNORING_FEBRUARY_29)
                .getTransition())
        .isEqualTo(DST_START_J_IGNORING_FEB29_AMSTERDAM_2015);
  }

  @Test
  public void testDaylightSavingTimeEndAmsterdam2015JulianIgnoringFeb29() {
    final DaylightSavingTimeTransition dstTransition =
        new DaylightSavingTimeTransition(DST_END_J_IGNORING_FEB29_AMSTERDAM_2015);
    assertThat(dstTransition.getDateTimeForYear(2015).toDateTime(DateTimeZone.UTC))
        .isEqualTo(DST_END_DATE_TIME_AMSTERDAM_2015.toDateTime(DateTimeZone.UTC));

    assertThat(
            DaylightSavingTimeTransition.forDateTimeAccordingToFormat(
                    DST_END_DATE_TIME_AMSTERDAM_2015,
                    DaylightSavingTimeTransition.DstTransitionFormat
                        .JULIAN_DAY_IGNORING_FEBRUARY_29)
                .getTransition())
        .isEqualTo(DST_END_J_IGNORING_FEB29_AMSTERDAM_2015);
  }

  @Test
  public void testDaylightSavingTimeStartAmsterdam2016DayOfWeekOfMonth() {
    final DaylightSavingTimeTransition dstTransition =
        new DaylightSavingTimeTransition(DST_START_MWD_AMSTERDAM);
    assertThat(dstTransition.getDateTimeForYear(2016).toDateTime(DateTimeZone.UTC))
        .isEqualTo(DST_START_DATE_TIME_AMSTERDAM_2016.toDateTime(DateTimeZone.UTC));

    assertThat(
            DaylightSavingTimeTransition.forDateTimeAccordingToFormat(
                    DST_START_DATE_TIME_AMSTERDAM_2016,
                    DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH)
                .getTransition())
        .isEqualTo(DST_START_MWD_AMSTERDAM);
  }

  @Test
  public void testDaylightSavingTimeEndAmsterdam2016DayOfWeekOfMonth() {
    final DaylightSavingTimeTransition dstTransition =
        new DaylightSavingTimeTransition(DST_END_MWD_AMSTERDAM);
    assertThat(dstTransition.getDateTimeForYear(2016).toDateTime(DateTimeZone.UTC))
        .isEqualTo(DST_END_DATE_TIME_AMSTERDAM_2016.toDateTime(DateTimeZone.UTC));

    assertThat(
            DaylightSavingTimeTransition.forDateTimeAccordingToFormat(
                    DST_END_DATE_TIME_AMSTERDAM_2016,
                    DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH)
                .getTransition())
        .isEqualTo(DST_END_MWD_AMSTERDAM);
  }

  @Test
  public void testDaylightSavingTimeStartAmsterdam2016JulianCountingFeb29() {
    final DaylightSavingTimeTransition dstTransition =
        new DaylightSavingTimeTransition(DST_START_J_COUNTING_FEB29_AMSTERDAM_2016);
    assertThat(dstTransition.getDateTimeForYear(2016).toDateTime(DateTimeZone.UTC))
        .isEqualTo(DST_START_DATE_TIME_AMSTERDAM_2016.toDateTime(DateTimeZone.UTC));

    assertThat(
            DaylightSavingTimeTransition.forDateTimeAccordingToFormat(
                    DST_START_DATE_TIME_AMSTERDAM_2016,
                    DaylightSavingTimeTransition.DstTransitionFormat
                        .JULIAN_DAY_COUNTING_FEBRUARY_29)
                .getTransition())
        .isEqualTo(DST_START_J_COUNTING_FEB29_AMSTERDAM_2016);
  }

  @Test
  public void testDaylightSavingTimeEndAmsterdam2016JulianCountingFeb29() {
    final DaylightSavingTimeTransition dstTransition =
        new DaylightSavingTimeTransition(DST_END_J_COUNTING_FEB29_AMSTERDAM_2016);
    assertThat(dstTransition.getDateTimeForYear(2016).toDateTime(DateTimeZone.UTC))
        .isEqualTo(DST_END_DATE_TIME_AMSTERDAM_2016.toDateTime(DateTimeZone.UTC));

    assertThat(
            DaylightSavingTimeTransition.forDateTimeAccordingToFormat(
                    DST_END_DATE_TIME_AMSTERDAM_2016,
                    DaylightSavingTimeTransition.DstTransitionFormat
                        .JULIAN_DAY_COUNTING_FEBRUARY_29)
                .getTransition())
        .isEqualTo(DST_END_J_COUNTING_FEB29_AMSTERDAM_2016);
  }

  @Test
  public void testDaylightSavingTimeStartAmsterdam2016JulianIgnoringFeb29() {
    final DaylightSavingTimeTransition dstTransition =
        new DaylightSavingTimeTransition(DST_START_J_IGNORING_FEB29_AMSTERDAM_2016);
    assertThat(dstTransition.getDateTimeForYear(2016).toDateTime(DateTimeZone.UTC))
        .isEqualTo(DST_START_DATE_TIME_AMSTERDAM_2016.toDateTime(DateTimeZone.UTC));

    assertThat(
            DaylightSavingTimeTransition.forDateTimeAccordingToFormat(
                    DST_START_DATE_TIME_AMSTERDAM_2016,
                    DaylightSavingTimeTransition.DstTransitionFormat
                        .JULIAN_DAY_IGNORING_FEBRUARY_29)
                .getTransition())
        .isEqualTo(DST_START_J_IGNORING_FEB29_AMSTERDAM_2016);
  }

  @Test
  public void testDaylightSavingTimeEndAmsterdam2016JulianIgnoringFeb29() {
    final DaylightSavingTimeTransition dstTransition =
        new DaylightSavingTimeTransition(DST_END_J_IGNORING_FEB29_AMSTERDAM_2016);
    assertThat(dstTransition.getDateTimeForYear(2016).toDateTime(DateTimeZone.UTC))
        .isEqualTo(DST_END_DATE_TIME_AMSTERDAM_2016.toDateTime(DateTimeZone.UTC));

    assertThat(
            DaylightSavingTimeTransition.forDateTimeAccordingToFormat(
                    DST_END_DATE_TIME_AMSTERDAM_2016,
                    DaylightSavingTimeTransition.DstTransitionFormat
                        .JULIAN_DAY_IGNORING_FEBRUARY_29)
                .getTransition())
        .isEqualTo(DST_END_J_IGNORING_FEB29_AMSTERDAM_2016);
  }

  @Test
  public void testJulianIgnoringFebTransitionValidationExceedsRangeOfUnit() {
    // Days aren't 0 indexed when ignoring feb 29
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29
                .isValid("J0"))
        .isFalse();
    // A year (without feb 29) doesn't have more than 365 days
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29
                .isValid("J366"))
        .isFalse();
    // Time unit may not equal or exceed 7 days
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29
                .isValid("J88/-168"))
        .isFalse();
    // Time unit may not equal or exceed 7 days
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29
                .isValid("J88/168"))
        .isFalse();
  }

  @Test
  public void testJulianIgnoringFebTransitionValidation() {
    // Valid transition without time
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29
                .isValid("J88"))
        .isTrue();

    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29
                .isValid(null))
        .isFalse();
    // Invalid format
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29
                .isValid("M3.5.0/2"))
        .isFalse();
    // No day count defined
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29
                .isValid("J/2"))
        .isFalse();
    // Invalid number as day count
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29
                .isValid("Jd/2"))
        .isFalse();
    // Time is signaled but not provided
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29
                .isValid("J88/"))
        .isFalse();
    // Invalid format (empty string)
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29
                .isValid(""))
        .isFalse();

    assertThatThrownBy(() -> new DaylightSavingTimeTransition("J88/"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void testJulianTransitionValidationExceedsRangeOfUnit() {
    // Days are 0 indexed so -1 is invalid
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29
                .isValid("-1"))
        .isFalse();
    // There aren't more than 365 days (0 indexed)
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29
                .isValid("366"))
        .isFalse();
    // Time unit may not equal or exceed 7 days
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29
                .isValid("88/-168"))
        .isFalse();
    // Time unit may not equal or exceed 7 days
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29
                .isValid("88/168"))
        .isFalse();
  }

  @Test
  public void testJulianTransitionValidation() {
    // Valid transition without time
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29
                .isValid("88"))
        .isTrue();

    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29
                .isValid(null))
        .isFalse();
    // Invalid format
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29
                .isValid("M3.5.0/2"))
        .isFalse();
    // No day count defined
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29
                .isValid("/2"))
        .isFalse();
    // Invalid number as day count
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29
                .isValid("d/2"))
        .isFalse();
    // Time is signaled but not provided
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29
                .isValid("88/"))
        .isFalse();
    // Invalid format (empty string)
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29
                .isValid(""))
        .isFalse();

    assertThatThrownBy(() -> new DaylightSavingTimeTransition("88/"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void testDayOfWeekOfMonthTransitionValidationExceedsRangeOfUnit() {
    // Month aren't 0 indexed therefore there is no month 0
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValid("M0.5.0"))
        .isFalse();
    // There aren't more than 12 months
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValid(
                "M13.5.0"))
        .isFalse();
    // Week number within a month aren't 0 indexed therefore there is no
    // week 0
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValid("M3.0.0"))
        .isFalse();
    // There can never be 6 occurrences of a day in a month
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValid("M3.6.0"))
        .isFalse();
    // Days are 0 indexed therefore there is no day lower than 0 or higher
    // than 6
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValid(
                "M3.5.-1"))
        .isFalse();
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValid("M3.5.7"))
        .isFalse();
    // Hours are 0 indexed and the 23'rd hour would mean the next day
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValid(
                "M3.5.7/23"))
        .isFalse();
    // Hours are 0 indexed therefore the hour count cannot be lower than 0
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValid(
                "M3.5.7/-1"))
        .isFalse();
  }

  @Test
  public void testDayOfWeekOfMonthTransitionValidation() {
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValid("M3.5.0"))
        .isTrue();

    assertThat(DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValid(null))
        .isFalse();
    // Invalid format
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValid("J88/2"))
        .isFalse();
    // No day number defined
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValid("M3.5/2"))
        .isFalse();
    // Invalid numbers
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValid(
                "Mm.n.d/2"))
        .isFalse();
    // Time is signaled but not provided
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValid(
                "M3.5.0/"))
        .isFalse();
    // Invalid format (empty string)
    assertThat(DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValid(""))
        .isFalse();

    assertThatThrownBy(() -> new DaylightSavingTimeTransition("M3.5.0/"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void testGetDateTime() {
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29
                .getDateTime(amsterdamDTZ, DST_START_J_IGNORING_FEB29_AMSTERDAM_2015, 2015))
        .isEqualByComparingTo(DST_START_DATE_TIME_AMSTERDAM_2015);
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29
                .getDateTime(amsterdamDTZ, DST_START_J_COUNTING_FEB29_AMSTERDAM_2015, 2015))
        .isEqualByComparingTo(DST_START_DATE_TIME_AMSTERDAM_2015);
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.getDateTime(
                amsterdamDTZ, DST_START_MWD_AMSTERDAM, 2015))
        .isEqualByComparingTo(DST_START_DATE_TIME_AMSTERDAM_2015);

    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29
                .getDateTime(amsterdamDTZ, "J327", 2015))
        .isEqualByComparingTo(DateTime.parse("2015-11-23T00:00:00.000+01:00"));
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29
                .getDateTime(amsterdamDTZ, "326", 2015))
        .isEqualByComparingTo(DateTime.parse("2015-11-23T00:00:00.000+01:00"));
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.getDateTime(
                amsterdamDTZ, "M11.4.1", 2015))
        .isEqualByComparingTo(DateTime.parse("2015-11-23T00:00:00.000+01:00"));

    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29
                .getDateTime(amsterdamDTZ, "J1", 2016))
        .isEqualByComparingTo(DateTime.parse("2016-01-1T00:00:00.000+01:00"));
  }

  @Test
  public void testGetDaylightSavingTimeTransition() {
    final DateTime midNight = DateTime.parse("2015-01-1T00:00:00.000+01:00");
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29
                .getDaylightSavingTimeTransition(midNight)
                .getTransition())
        .isEqualTo("J1");
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29
                .getDaylightSavingTimeTransition(midNight)
                .getTransition())
        .isEqualTo("0");
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH
                .getDaylightSavingTimeTransition(midNight)
                .getTransition())
        .isEqualTo("M1.1.4");
  }

  @Test
  public void testIsValidTime() {
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValidTime(
                "2:30"))
        .isTrue();
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValidTime(
                "2:30:30"))
        .isTrue();

    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValidTime(null))
        .isFalse();
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValidTime(""))
        .isFalse();
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValidTime(
                "2:60"))
        .isFalse();
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValidTime(
                "2:-1"))
        .isFalse();
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValidTime("h"))
        .isFalse();
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValidTime(
                "2:m"))
        .isFalse();
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValidTime(
                "2:30:30:900"))
        .isFalse();
  }

  @Test
  public void testGetTime() {
    assertThat(DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.getTime("50"))
        .isEqualTo(0);
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.getTime(
                "M3.5.0/2"))
        .isEqualTo(2);
    assertThat(
            DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.getTime(
                "M3.5.0/2:30"))
        .isEqualTo(2);
  }

  @Test
  public void testGetDateTimeForNextTransition() {
    final DateTime dateTimeBeforeDay30 = DateTime.parse("2015-01-01T00:00:00.000+01:00");
    final DateTime dateTimeAfterDay30 = DateTime.parse("2015-06-01T00:00:00.000+01:00");
    final DateTime dateTimeOfDay30ThisYear = DateTime.parse("2015-01-31T00:00:00.000+01:00");
    final DateTime dateTimeOfDay30NextYear = DateTime.parse("2016-01-31T00:00:00.000+01:00");

    assertThat(
            (new DaylightSavingTimeTransition("30"))
                .getDateTimeForNextTransition(dateTimeBeforeDay30))
        .isEqualByComparingTo(dateTimeOfDay30ThisYear);
    assertThat(
            (new DaylightSavingTimeTransition("30"))
                .getDateTimeForNextTransition(dateTimeAfterDay30))
        .isEqualByComparingTo(dateTimeOfDay30NextYear);
  }
}
