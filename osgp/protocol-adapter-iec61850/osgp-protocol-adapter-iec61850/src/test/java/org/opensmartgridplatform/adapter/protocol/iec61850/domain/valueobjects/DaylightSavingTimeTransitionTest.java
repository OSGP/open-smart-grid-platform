package org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects;

import static org.assertj.core.api.Assertions.assertThat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

public class DaylightSavingTimeTransitionTest {

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

    public static final DateTime DST_START_DATE_TIME_AMSTERDAM_2015 = DateTime.parse("2015-03-29T02:00:00.000+01:00");
    public static final DateTime DST_END_DATE_TIME_AMSTERDAM_2015 = DateTime.parse("2015-10-25T03:00:00.000+02:00");
    public static final DateTime DST_START_DATE_TIME_AMSTERDAM_2016 = DateTime.parse("2016-03-27T02:00:00.000+01:00");
    public static final DateTime DST_END_DATE_TIME_AMSTERDAM_2016 = DateTime.parse("2016-10-30T03:00:00.000+02:00");

    @Test
    public void testDaylightSavingTimeStartAmsterdam2015DayOfWeekOfMonth() throws Exception {

        final DaylightSavingTimeTransition dstTransition = new DaylightSavingTimeTransition(DST_START_MWD_AMSTERDAM);
        assertThat(dstTransition.getDateTimeForYear(2015).toDateTime(DateTimeZone.UTC))
                .isEqualTo(DST_START_DATE_TIME_AMSTERDAM_2015.toDateTime(DateTimeZone.UTC));

        assertThat(
                DaylightSavingTimeTransition
                        .forDateTimeAccordingToFormat(DST_START_DATE_TIME_AMSTERDAM_2015,
                                DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH)
                        .getTransition()).isEqualTo(DST_START_MWD_AMSTERDAM);
    }

    @Test
    public void testDaylightSavingTimeEndAmsterdam2015DayOfWeekOfMonth() throws Exception {

        final DaylightSavingTimeTransition dstTransition = new DaylightSavingTimeTransition(DST_END_MWD_AMSTERDAM);
        assertThat(dstTransition.getDateTimeForYear(2015).toDateTime(DateTimeZone.UTC))
                .isEqualTo(DST_END_DATE_TIME_AMSTERDAM_2015.toDateTime(DateTimeZone.UTC));

        assertThat(
                DaylightSavingTimeTransition
                        .forDateTimeAccordingToFormat(DST_END_DATE_TIME_AMSTERDAM_2015,
                                DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH)
                        .getTransition()).isEqualTo(DST_END_MWD_AMSTERDAM);
    }

    @Test
    public void testDaylightSavingTimeStartAmsterdam2015JulianCountingFeb29() throws Exception {

        final DaylightSavingTimeTransition dstTransition = new DaylightSavingTimeTransition(
                DST_START_J_COUNTING_FEB29_AMSTERDAM_2015);
        assertThat(dstTransition.getDateTimeForYear(2015).toDateTime(DateTimeZone.UTC))
                .isEqualTo(DST_START_DATE_TIME_AMSTERDAM_2015.toDateTime(DateTimeZone.UTC));

        assertThat(DaylightSavingTimeTransition
                .forDateTimeAccordingToFormat(DST_START_DATE_TIME_AMSTERDAM_2015,
                        DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29)
                .getTransition()).isEqualTo(DST_START_J_COUNTING_FEB29_AMSTERDAM_2015);
    }

    @Test
    public void testDaylightSavingTimeEndAmsterdam2015JulianCountingFeb29() throws Exception {

        final DaylightSavingTimeTransition dstTransition = new DaylightSavingTimeTransition(
                DST_END_J_COUNTING_FEB29_AMSTERDAM_2015);
        assertThat(dstTransition.getDateTimeForYear(2015).toDateTime(DateTimeZone.UTC))
                .isEqualTo(DST_END_DATE_TIME_AMSTERDAM_2015.toDateTime(DateTimeZone.UTC));

        assertThat(DaylightSavingTimeTransition
                .forDateTimeAccordingToFormat(DST_END_DATE_TIME_AMSTERDAM_2015,
                        DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29)
                .getTransition()).isEqualTo(DST_END_J_COUNTING_FEB29_AMSTERDAM_2015);
    }

    @Test
    public void testDaylightSavingTimeStartAmsterdam2015JulianIgnoringFeb29() throws Exception {

        final DaylightSavingTimeTransition dstTransition = new DaylightSavingTimeTransition(
                DST_START_J_IGNORING_FEB29_AMSTERDAM_2015);
        assertThat(dstTransition.getDateTimeForYear(2015).toDateTime(DateTimeZone.UTC))
                .isEqualTo(DST_START_DATE_TIME_AMSTERDAM_2015.toDateTime(DateTimeZone.UTC));

        assertThat(DaylightSavingTimeTransition
                .forDateTimeAccordingToFormat(DST_START_DATE_TIME_AMSTERDAM_2015,
                        DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29)
                .getTransition()).isEqualTo(DST_START_J_IGNORING_FEB29_AMSTERDAM_2015);
    }

    @Test
    public void testDaylightSavingTimeEndAmsterdam2015JulianIgnoringFeb29() throws Exception {

        final DaylightSavingTimeTransition dstTransition = new DaylightSavingTimeTransition(
                DST_END_J_IGNORING_FEB29_AMSTERDAM_2015);
        assertThat(dstTransition.getDateTimeForYear(2015).toDateTime(DateTimeZone.UTC))
                .isEqualTo(DST_END_DATE_TIME_AMSTERDAM_2015.toDateTime(DateTimeZone.UTC));

        assertThat(DaylightSavingTimeTransition
                .forDateTimeAccordingToFormat(DST_END_DATE_TIME_AMSTERDAM_2015,
                        DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29)
                .getTransition()).isEqualTo(DST_END_J_IGNORING_FEB29_AMSTERDAM_2015);
    }

    @Test
    public void testDaylightSavingTimeStartAmsterdam2016DayOfWeekOfMonth() throws Exception {

        final DaylightSavingTimeTransition dstTransition = new DaylightSavingTimeTransition(DST_START_MWD_AMSTERDAM);
        assertThat(dstTransition.getDateTimeForYear(2016).toDateTime(DateTimeZone.UTC))
                .isEqualTo(DST_START_DATE_TIME_AMSTERDAM_2016.toDateTime(DateTimeZone.UTC));

        assertThat(
                DaylightSavingTimeTransition
                        .forDateTimeAccordingToFormat(DST_START_DATE_TIME_AMSTERDAM_2016,
                                DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH)
                        .getTransition()).isEqualTo(DST_START_MWD_AMSTERDAM);
    }

    @Test
    public void testDaylightSavingTimeEndAmsterdam2016DayOfWeekOfMonth() throws Exception {

        final DaylightSavingTimeTransition dstTransition = new DaylightSavingTimeTransition(DST_END_MWD_AMSTERDAM);
        assertThat(dstTransition.getDateTimeForYear(2016).toDateTime(DateTimeZone.UTC))
                .isEqualTo(DST_END_DATE_TIME_AMSTERDAM_2016.toDateTime(DateTimeZone.UTC));

        assertThat(
                DaylightSavingTimeTransition
                        .forDateTimeAccordingToFormat(DST_END_DATE_TIME_AMSTERDAM_2016,
                                DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH)
                        .getTransition()).isEqualTo(DST_END_MWD_AMSTERDAM);
    }

    @Test
    public void testDaylightSavingTimeStartAmsterdam2016JulianCountingFeb29() throws Exception {

        final DaylightSavingTimeTransition dstTransition = new DaylightSavingTimeTransition(
                DST_START_J_COUNTING_FEB29_AMSTERDAM_2016);
        assertThat(dstTransition.getDateTimeForYear(2016).toDateTime(DateTimeZone.UTC))
                .isEqualTo(DST_START_DATE_TIME_AMSTERDAM_2016.toDateTime(DateTimeZone.UTC));

        assertThat(DaylightSavingTimeTransition
                .forDateTimeAccordingToFormat(DST_START_DATE_TIME_AMSTERDAM_2016,
                        DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29)
                .getTransition()).isEqualTo(DST_START_J_COUNTING_FEB29_AMSTERDAM_2016);
    }

    @Test
    public void testDaylightSavingTimeEndAmsterdam2016JulianCountingFeb29() throws Exception {

        final DaylightSavingTimeTransition dstTransition = new DaylightSavingTimeTransition(
                DST_END_J_COUNTING_FEB29_AMSTERDAM_2016);
        assertThat(dstTransition.getDateTimeForYear(2016).toDateTime(DateTimeZone.UTC))
                .isEqualTo(DST_END_DATE_TIME_AMSTERDAM_2016.toDateTime(DateTimeZone.UTC));

        assertThat(DaylightSavingTimeTransition
                .forDateTimeAccordingToFormat(DST_END_DATE_TIME_AMSTERDAM_2016,
                        DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29)
                .getTransition()).isEqualTo(DST_END_J_COUNTING_FEB29_AMSTERDAM_2016);
    }

    @Test
    public void testDaylightSavingTimeStartAmsterdam2016JulianIgnoringFeb29() throws Exception {

        final DaylightSavingTimeTransition dstTransition = new DaylightSavingTimeTransition(
                DST_START_J_IGNORING_FEB29_AMSTERDAM_2016);
        assertThat(dstTransition.getDateTimeForYear(2016).toDateTime(DateTimeZone.UTC))
                .isEqualTo(DST_START_DATE_TIME_AMSTERDAM_2016.toDateTime(DateTimeZone.UTC));

        assertThat(DaylightSavingTimeTransition
                .forDateTimeAccordingToFormat(DST_START_DATE_TIME_AMSTERDAM_2016,
                        DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29)
                .getTransition()).isEqualTo(DST_START_J_IGNORING_FEB29_AMSTERDAM_2016);
    }

    @Test
    public void testDaylightSavingTimeEndAmsterdam2016JulianIgnoringFeb29() throws Exception {

        final DaylightSavingTimeTransition dstTransition = new DaylightSavingTimeTransition(
                DST_END_J_IGNORING_FEB29_AMSTERDAM_2016);
        assertThat(dstTransition.getDateTimeForYear(2016).toDateTime(DateTimeZone.UTC))
                .isEqualTo(DST_END_DATE_TIME_AMSTERDAM_2016.toDateTime(DateTimeZone.UTC));

        assertThat(DaylightSavingTimeTransition
                .forDateTimeAccordingToFormat(DST_END_DATE_TIME_AMSTERDAM_2016,
                        DaylightSavingTimeTransition.DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29)
                .getTransition()).isEqualTo(DST_END_J_IGNORING_FEB29_AMSTERDAM_2016);
    }

    @Test
    public void testDayOfWeekOfMonthTransitionValidationExceedsMaxMonth() {
        assertThat(DaylightSavingTimeTransition.DstTransitionFormat.DAY_OF_WEEK_OF_MONTH.isValid("M13.5.0")).isFalse();
    }
}
