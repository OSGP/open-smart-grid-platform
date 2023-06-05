// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

public class CosemDateTime implements Serializable, Comparable<CosemDateTime> {

  private static final long serialVersionUID = 4157582293990514746L;

  private static final int MILLISECONDS_PER_MINUTE = 60 * 1000;

  public static final int DEVIATION_NOT_SPECIFIED = 0x8000;

  private final CosemDate date;
  private final CosemTime time;

  private final int deviation;

  private final ClockStatus clockStatus;

  public CosemDateTime(
      final CosemDate date,
      final CosemTime time,
      final int deviation,
      final ClockStatus clockStatus) {
    Objects.requireNonNull(date, "date must not be null");
    Objects.requireNonNull(time, "time must not be null");
    Objects.requireNonNull(clockStatus, "clockStatus must not be null");
    this.checkDeviation(deviation);
    this.date = new CosemDate(date);
    this.time = new CosemTime(time);
    this.deviation = deviation;
    this.clockStatus = new ClockStatus(clockStatus);
  }

  public CosemDateTime(
      final LocalDate date,
      final LocalTime time,
      final int deviation,
      final ClockStatus clockStatus) {
    this(new CosemDate(date), new CosemTime(time), deviation, clockStatus);
  }

  public CosemDateTime(final LocalDate date, final LocalTime time, final int deviation) {
    this(
        new CosemDate(date),
        new CosemTime(time),
        deviation,
        new ClockStatus(ClockStatus.STATUS_NOT_SPECIFIED));
  }

  public CosemDateTime(
      final LocalDateTime dateTime, final int deviation, final ClockStatus clockStatus) {
    this(dateTime.toLocalDate(), dateTime.toLocalTime(), deviation, clockStatus);
  }

  public CosemDateTime(final LocalDateTime dateTime, final int deviation) {
    this(dateTime.toLocalDate(), dateTime.toLocalTime(), deviation);
  }

  public CosemDateTime(final DateTime dateTime) {
    this(
        dateTime.toLocalDate(),
        dateTime.toLocalTime(),
        determineDeviation(dateTime),
        determineClockStatus(dateTime));
  }

  public CosemDateTime(final CosemDateTime cosemDateTime) {
    this(
        cosemDateTime.getDate(),
        cosemDateTime.getTime(),
        cosemDateTime.getDeviation(),
        cosemDateTime.getClockStatus());
  }

  private void checkDeviation(final int deviation) {
    if (!this.isValidDeviation(deviation)) {
      throw new IllegalArgumentException("Deviation not in [-720..720, 0x8000]: " + deviation);
    }
  }

  private boolean isValidDeviation(final int deviation) {
    return this.isSpecificDeviation(deviation) || DEVIATION_NOT_SPECIFIED == deviation;
  }

  private boolean isSpecificDeviation(final int deviation) {
    return deviation >= -720 && deviation <= 720;
  }

  private static int determineDeviation(final DateTime dateTime) {
    return -(dateTime.getZone().getOffset(dateTime.getMillis()) / MILLISECONDS_PER_MINUTE);
  }

  private static ClockStatus determineClockStatus(final DateTime dateTime) {
    final Set<ClockStatusBit> statusBits = EnumSet.noneOf(ClockStatusBit.class);
    if (!dateTime.getZone().isStandardOffset(dateTime.getMillis())) {
      statusBits.add(ClockStatusBit.DAYLIGHT_SAVING_ACTIVE);
    }
    return new ClockStatus(statusBits);
  }

  public CosemDateTime() {
    this(DateTime.now());
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("CosemDateTime[");
    sb.append(this.date);
    sb.append(' ');
    sb.append(this.time);
    sb.append(", deviation=" + this.deviation);
    sb.append(", " + this.clockStatus);
    return sb.append(']').toString();
  }

  public CosemDate getDate() {
    return this.date;
  }

  public CosemTime getTime() {
    return this.time;
  }

  public int getDeviation() {
    return this.deviation;
  }

  public ClockStatus getClockStatus() {
    return this.clockStatus;
  }

  public boolean isDeviationSpecified() {
    return DEVIATION_NOT_SPECIFIED != this.deviation;
  }

  /**
   * @return {@code true} if the date, time and deviation are specified; {@code false} otherwise.
   * @see #isLocalDateTimeSpecified()
   * @see #isDeviationSpecified()
   */
  public boolean isDateTimeSpecified() {
    return this.isLocalDateTimeSpecified() && this.isDeviationSpecified();
  }

  /**
   * Returns this {@link CosemDateTime} as {@link DateTime} if the date, time and deviation are
   * specified.
   *
   * @return this {@link CosemDateTime} as {@link DateTime}, or {@code null} if not {@link
   *     #isDateTimeSpecified()}.
   * @see #isDateTimeSpecified()
   */
  public DateTime asDateTime() {
    if (!this.isDateTimeSpecified()) {
      return null;
    }
    final LocalDateTime localDateTime = this.asLocalDateTime();
    final DateTimeZone zone =
        DateTimeZone.forOffsetMillis(-this.deviation * MILLISECONDS_PER_MINUTE);
    return localDateTime.toDateTime(zone);
  }

  /**
   * @return {@code true} if the date and time are specified; {@code false} otherwise.
   * @see #isLocalDateSpecified()
   * @see #isLocalTimeSpecified()
   */
  public boolean isLocalDateTimeSpecified() {
    return this.date.isLocalDateSpecified() && this.time.isLocalTimeSpecified();
  }

  /**
   * Returns this {@link CosemDateTime} as {@link LocalDateTime} if the date and time are specified.
   *
   * @return this {@link CosemDateTime} as {@link LocalDateTime}, or {@code null} if not {@link
   *     #isLocalDateTimeSpecified()}.
   * @see #isLocalDateTimeSpecified()
   */
  public LocalDateTime asLocalDateTime() {
    if (!this.isLocalDateTimeSpecified()) {
      return null;
    }
    if (this.time.isSecondNotSpecified()) {
      return new LocalDateTime(
          this.date.getYear(),
          this.date.getMonth(),
          this.date.getDayOfMonth(),
          this.time.getHour(),
          this.time.getMinute());
    }
    if (this.time.isHundredthsNotSpecified()) {
      return new LocalDateTime(
          this.date.getYear(),
          this.date.getMonth(),
          this.date.getDayOfMonth(),
          this.time.getHour(),
          this.time.getMinute(),
          this.time.getSecond());
    }
    return new LocalDateTime(
        this.date.getYear(),
        this.date.getMonth(),
        this.date.getDayOfMonth(),
        this.time.getHour(),
        this.time.getMinute(),
        this.time.getSecond(),
        this.time.getHundredths() * 10);
  }

  /**
   * @return {@code true} if the date is specified; {@code false} otherwise.
   * @see #getDate()
   * @see CosemDate#isLocalDateSpecified()
   */
  public boolean isLocalDateSpecified() {
    return this.date.isLocalDateSpecified();
  }

  /**
   * Returns this {@link CosemDateTime} as {@link LocalDate} if the date is specified.
   *
   * @return this {@link CosemDateTime} as {@link LocalDate}, or {@code null} if not {@link
   *     #isLocalDateSpecified()}.
   * @see #isLocalDateSpecified()
   */
  public LocalDate asLocalDate() {
    return this.date.asLocalDate();
  }

  /**
   * @return {@code true} if the time is specified; {@code false} otherwise.
   * @see #getTime()
   * @see CosemTime#isLocalTimeSpecified()
   */
  public boolean isLocalTimeSpecified() {
    return this.time.isLocalTimeSpecified();
  }

  /**
   * Returns this {@link CosemDateTime} as {@link LocalTime} if the time is specified.
   *
   * @return this {@link CosemDateTime} as {@link LocalTime}, or {@code null} if not {@link
   *     #isLocalTimeSpecified()}.
   * @see #isLocalTimeSpecified()
   */
  public LocalTime asLocalTime() {
    return this.time.asLocalTime();
  }

  @Override
  public int compareTo(final CosemDateTime o) {
    // If a valid datetime can be created, use this to compare.
    // This will take deviation in to account.
    final LocalDateTime timeThis = this.asLocalDateTime();
    final LocalDateTime timeOther = o.asLocalDateTime();
    if (timeThis != null && timeOther != null) {
      return timeThis.compareTo(timeOther);
    }

    // Otherwise compare date/time on an byte value basis.
    // Taking deviation into account is complex and bluebook
    // does not describe how that should even work with unspecified values.
    final int compDate = this.date.compareTo(o.date);
    if (compDate != 0) {
      return compDate;
    }

    final int compTime = this.time.compareTo(o.time);
    if (compTime != 0) {
      return compTime;
    }

    return 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.date, this.time, this.clockStatus, this.deviation);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final CosemDateTime other = (CosemDateTime) obj;

    return Objects.equals(this.date, other.date)
        && Objects.equals(this.time, other.time)
        && Objects.equals(this.clockStatus, other.clockStatus)
        && this.deviation == other.deviation;
  }
}
