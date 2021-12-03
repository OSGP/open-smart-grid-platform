/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects;

import java.util.Objects;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;

/**
 * Class for handling representations of the start (when DST goes into effect) or end time (when
 * time goes back to standard time) for Daylight Savings Time according to time zone specifications
 * in POSIX systems.
 *
 * <p>Of the formats specified only the Julian day and the day of week of month variations are
 * supported.
 *
 * <p>This implementation considers any time based offsets in hours to be based on the locale for
 * "Europe/Amsterdam", unless another timezone is provided explicitly.
 *
 * @see http://www.gnu.org/software/libc/manual/html_node/TZ-Variable.html
 */
public class DaylightSavingTimeTransition {

  public enum DstTransitionFormat {
    /**
     *
     *
     * <dl>
     *   <dt>Jn
     *   <dd>Julian day, with {@code n} between {@code 1} and {@code 365}. February 29 is never
     *       counted.
     * </dl>
     */
    JULIAN_DAY_IGNORING_FEBRUARY_29 {
      @Override
      public boolean isValid(final String transition) {
        if (transition == null || transition.isEmpty() || 'J' != transition.charAt(0)) {
          return false;
        }
        final int timeSeparatorPos = transition.indexOf('/');
        final int n;
        try {
          if (timeSeparatorPos == -1) {
            n = Integer.parseInt(transition.substring(1));
          } else {
            n = Integer.parseInt(transition.substring(1, timeSeparatorPos));
          }
        } catch (final NumberFormatException nfe) {
          return false;
        }
        if (n < 1 || n > 365) {
          return false;
        }
        if (timeSeparatorPos == -1) {
          return true;
        }
        return this.isValidTime(transition.substring(timeSeparatorPos + 1));
      }

      @Override
      public DaylightSavingTimeTransition getDaylightSavingTimeTransition(final DateTime dateTime) {
        final int dayOfYear = dateTime.getDayOfYear();
        final int n;
        if (dateTime.getMonthOfYear() < DateTimeConstants.MARCH
            || !dateTime.toGregorianCalendar().isLeapYear(dateTime.getYear())) {
          n = dayOfYear;
        } else {
          /*
           * Leap year, date on or after February 29th, ignore leap
           * day by subtracting 1.
           */
          n = dayOfYear - 1;
        }
        final int hours = dateTime.getHourOfDay();
        final String transition;
        if (hours == 0) {
          transition = "J" + n;
        } else {
          transition = "J" + n + "/" + hours;
        }
        return new DaylightSavingTimeTransition(dateTime.getZone(), transition);
      }

      @Override
      public DateTime getDateTime(
          final DateTimeZone dateTimeZone, final String transition, final int year) {
        final int timeSeparatorPos = transition.indexOf('/');
        final int n;
        if (timeSeparatorPos == -1) {
          n = Integer.parseInt(transition.substring(1));
        } else {
          n = Integer.parseInt(transition.substring(1, timeSeparatorPos));
        }

        final DateTime candidate = new DateTime(year, 1, 1, 0, 0, 0, 0, dateTimeZone).plusDays(n);

        final boolean subtractOne =
            !candidate.toGregorianCalendar().isLeapYear(year)
                || candidate.getMonthOfYear() < DateTimeConstants.MARCH;

        if (subtractOne) {
          return candidate.minusDays(1).plusHours(this.getTime(transition));
        }

        return candidate.plusHours(this.getTime(transition));
      }
    },
    /**
     *
     *
     * <dl>
     *   <dt>n
     *   <dd>Julian day, with {@code n} between {@code 0} and {@code 365}. February 29 is counted in
     *       leap years.
     * </dl>
     */
    JULIAN_DAY_COUNTING_FEBRUARY_29 {
      @Override
      public boolean isValid(final String transition) {
        if (transition == null) {
          return false;
        }
        final int timeSeparatorPos = transition.indexOf('/');
        final int n;
        try {
          if (timeSeparatorPos == -1) {
            n = Integer.parseInt(transition);
          } else {
            n = Integer.parseInt(transition.substring(0, timeSeparatorPos));
          }
        } catch (final NumberFormatException nfe) {
          return false;
        }
        if (n < 0 || n > 365) {
          return false;
        }
        if (timeSeparatorPos == -1) {
          return true;
        }
        return this.isValidTime(transition.substring(timeSeparatorPos + 1));
      }

      @Override
      public DaylightSavingTimeTransition getDaylightSavingTimeTransition(final DateTime dateTime) {
        final int n = dateTime.getDayOfYear() - 1;
        final int hours = dateTime.getHourOfDay();
        final String transition;
        if (hours == 0) {
          transition = String.valueOf(n);
        } else {
          transition = n + "/" + hours;
        }
        return new DaylightSavingTimeTransition(dateTime.getZone(), transition);
      }

      @Override
      public DateTime getDateTime(
          final DateTimeZone dateTimeZone, final String transition, final int year) {
        final int timeSeparatorPos = transition.indexOf('/');
        final int n;
        if (timeSeparatorPos == -1) {
          n = Integer.parseInt(transition);
        } else {
          n = Integer.parseInt(transition.substring(0, timeSeparatorPos));
        }
        return new DateTime(year, 1, 1, 0, 0, 0, 0, dateTimeZone)
            .plusDays(n)
            .plusHours(this.getTime(transition));
      }
    },
    /**
     *
     *
     * <dl>
     *   <dt>Mm.w.d
     *   <dd>Day {@code d} of week {@code w} of month {@code m}. Day {@code d} from {@code 0}
     *       (Sunday) to {@code 6}. Week {@code w} between {@code 1} (first week in which day {@code
     *       d} occurs) and {@code 5} (last day {@code d} of the month). Month {@code m} from {@code
     *       1} to {@code 12}.
     * </dl>
     */
    DAY_OF_WEEK_OF_MONTH {
      @Override
      public boolean isValid(final String transition) {
        if (transition == null || transition.isEmpty() || 'M' != transition.charAt(0)) {
          return false;
        }
        final int timeSeparatorPos = transition.indexOf('/');
        final String mwd;
        if (timeSeparatorPos == -1) {
          mwd = transition.substring(1);
        } else {
          mwd = transition.substring(1, timeSeparatorPos);
        }
        final String[] mwdParts = mwd.split("\\.");
        if (mwdParts.length != 3) {
          return false;
        }
        final int m;
        final int w;
        final int d;
        try {
          m = Integer.parseInt(mwdParts[0]);
          w = Integer.parseInt(mwdParts[1]);
          d = Integer.parseInt(mwdParts[2]);
        } catch (final NumberFormatException nfe) {
          return false;
        }
        return this.checkTransitionTime(transition, timeSeparatorPos, m, w, d);
      }

      private boolean checkTransitionTime(
          final String transition,
          final int timeSeparatorPos,
          final int m,
          final int w,
          final int d) {
        if (m < 1 || m > 12) {
          return false;
        }
        if (w < 1 || w > 5) {
          return false;
        }
        if (d < 0 || d > 6) {
          return false;
        }
        if (timeSeparatorPos == -1) {
          return true;
        }
        return this.isValidTime(transition.substring(timeSeparatorPos + 1));
      }

      @Override
      public DaylightSavingTimeTransition getDaylightSavingTimeTransition(final DateTime dateTime) {
        final int m = dateTime.getMonthOfYear();
        final boolean lastDayOfWeekForTheMonth = dateTime.plusDays(7).getMonthOfYear() > m;
        final int w = lastDayOfWeekForTheMonth ? 5 : 1 + ((dateTime.getDayOfMonth() - 1) / 7);
        final int d = dateTime.getDayOfWeek() % 7;
        final int time = dateTime.getHourOfDay();
        final String transition = "M" + m + "." + w + "." + d + (time == 0 ? "" : "/" + time);
        return new DaylightSavingTimeTransition(dateTime.getZone(), transition);
      }

      @Override
      public DateTime getDateTime(
          final DateTimeZone dateTimeZone, final String transition, final int year) {
        final int dotAfterM = transition.indexOf('.', 1);
        final int m = Integer.parseInt(transition.substring(1, dotAfterM));
        final int dotAfterW = transition.indexOf('.', dotAfterM + 1);
        final int w = Integer.parseInt(transition.substring(dotAfterM + 1, dotAfterW));
        final int timeSeparatorPos = transition.indexOf('/');
        final int d;
        if (timeSeparatorPos == -1) {
          d = Integer.parseInt(transition.substring(dotAfterW + 1));
        } else {
          d = Integer.parseInt(transition.substring(dotAfterW + 1, timeSeparatorPos));
        }
        final int dayOfWeek = d == 0 ? DateTimeConstants.SUNDAY : d;
        final int startAtDate = w == 5 ? 22 : (w - 1) * 7 + 1;
        final DateTime firstAttempt = new DateTime(year, m, startAtDate, 0, 0, 0, 0, dateTimeZone);
        final int dayDiff = dayOfWeek - firstAttempt.getDayOfWeek();
        final DateTime secondAttempt;
        if (dayDiff == 0) {
          secondAttempt = firstAttempt;
        } else {
          secondAttempt = firstAttempt.plusDays(dayDiff > 0 ? dayDiff : 7 + dayDiff);
        }
        if (w < 5) {
          return secondAttempt.plusHours(this.getTime(transition));
        }
        final DateTime thirdAttempt = secondAttempt.plusDays(7);
        if (thirdAttempt.getMonthOfYear() > secondAttempt.getMonthOfYear()) {
          return secondAttempt.plusHours(this.getTime(transition));
        }
        return thirdAttempt.plusHours(this.getTime(transition));
      }
    };

    public abstract boolean isValid(String transition);

    public abstract DaylightSavingTimeTransition getDaylightSavingTimeTransition(DateTime dateTime);

    public abstract DateTime getDateTime(DateTimeZone dateTimeZone, String transition, int year);

    public boolean isValidTime(final String time) {
      if (time == null) {
        return false;
      }
      final int hours;
      try {
        if (time.indexOf(':') == -1) {
          hours = Integer.parseInt(time);
        } else {
          final String[] timeParts = time.split(":");
          if (timeParts.length > 3) {
            return false;
          }
          hours = Integer.parseInt(timeParts[0]);
          for (int i = 1; i < timeParts.length; i++) {
            final int minutesOrSeconds = Integer.parseInt(timeParts[i]);
            if (minutesOrSeconds < 0 || minutesOrSeconds > 59) {
              return false;
            }
          }
        }
      } catch (final NumberFormatException nfe) {
        return false;
      }
      return hours >= -167 && hours <= 167;
    }

    public int getTime(final String transition) {
      final int timeSeparatorPos = transition.indexOf('/');
      if (timeSeparatorPos == -1) {
        return 0;
      }
      final int hourSeparatorPos = transition.indexOf(':', timeSeparatorPos + 1);
      if (hourSeparatorPos == -1) {
        return Integer.parseInt(transition.substring(timeSeparatorPos + 1));
      }
      return Integer.parseInt(transition.substring(timeSeparatorPos + 1, hourSeparatorPos));
    }
  }

  private static final DateTimeZone TIME_ZONE_AMSTERDAM = DateTimeZone.forID("Europe/Amsterdam");

  private final DstTransitionFormat format;
  private final String transition;
  private final DateTimeZone dateTimeZone;

  public static DaylightSavingTimeTransition forDateTimeAccordingToFormat(
      final DateTime dateTime, final DstTransitionFormat format) {
    Objects.requireNonNull(dateTime, "dateTime must not be null");
    Objects.requireNonNull(format, "format must not be null");

    return format.getDaylightSavingTimeTransition(dateTime);
  }

  /**
   * Creates a {@link DaylightSavingTimeTransition} for the given textual representation. The {@code
   * transition} must be a {@link String} according to one of the formats described by {@link
   * DstTransitionFormat}.
   *
   * @param dateTimeZone the time zone used for local time in hours
   * @param transition the formatted representation of when Daylight Saving Time goes into effect or
   *     when the change is made back to standard time.
   */
  public DaylightSavingTimeTransition(final DateTimeZone dateTimeZone, final String transition) {
    Objects.requireNonNull(dateTimeZone, "dateTimeZone must not be null");
    Objects.requireNonNull(transition, "transition must not be null");
    this.dateTimeZone = dateTimeZone;
    if (transition.startsWith("J")) {
      this.format = DstTransitionFormat.JULIAN_DAY_IGNORING_FEBRUARY_29;
    } else if (transition.startsWith("M")) {
      this.format = DstTransitionFormat.DAY_OF_WEEK_OF_MONTH;
    } else {
      this.format = DstTransitionFormat.JULIAN_DAY_COUNTING_FEBRUARY_29;
    }
    if (this.format.isValid(transition)) {
      this.transition = transition;
    } else {
      throw new IllegalArgumentException(
          "Transition is not a supported textual representation: " + transition);
    }
  }

  /**
   * Creates a {@link DaylightSavingTimeTransition} for time zone "Europe/Amsterdam".
   *
   * @see #DaylightSavingTimeTransition(DateTimeZone, String)
   */
  public DaylightSavingTimeTransition(final String transition) {
    this(TIME_ZONE_AMSTERDAM, transition);
  }

  public DateTime getDateTimeForYear(final int year) {
    return this.format.getDateTime(this.dateTimeZone, this.transition, year);
  }

  public DateTime getDateTimeForCurrentYear() {
    return this.getDateTimeForYear(DateTime.now(this.dateTimeZone).getYear());
  }

  public DateTime getDateTimeForNextTransition() {
    return this.getDateTimeForNextTransition(DateTime.now(this.dateTimeZone));
  }

  public DateTime getDateTimeForNextTransition(final DateTime dateTime) {
    final DateTime thisYearsTransition = this.getDateTimeForYear(dateTime.getYear());
    if (dateTime.isAfter(thisYearsTransition)) {
      return this.getDateTimeForYear(dateTime.getYear() + 1);
    }
    return thisYearsTransition;
  }

  public DstTransitionFormat getFormat() {
    return this.format;
  }

  public String getTransition() {
    return this.transition;
  }

  public DateTimeZone getDateTimeZone() {
    return this.dateTimeZone;
  }

  public int getTime() {
    return this.format.getTime(this.transition);
  }

  @Override
  public String toString() {
    return String.format("%s (%s)", this.transition, this.dateTimeZone);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof DaylightSavingTimeTransition)) {
      return false;
    }
    final DaylightSavingTimeTransition o = (DaylightSavingTimeTransition) obj;
    return Objects.equals(this.transition, o.transition)
        && Objects.equals(this.dateTimeZone, o.dateTimeZone);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.transition, this.dateTimeZone);
  }
}
