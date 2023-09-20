// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.time.*;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class CosemDateTimeDto implements Serializable, Comparable<CosemDateTimeDto> {

  private static final long serialVersionUID = 4157582293990514746L;

  private static final int MILLISECONDS_PER_SECOND = 1000;

  public static final int DEVIATION_NOT_SPECIFIED = 0x8000;

  private final CosemDateDto date;
  private final CosemTimeDto time;

  private final int deviation;

  private final ClockStatusDto clockStatus;

  public CosemDateTimeDto(
      final CosemDateDto date,
      final CosemTimeDto time,
      final int deviation,
      final ClockStatusDto clockStatus) {
    Objects.requireNonNull(date, "date must not be null");
    Objects.requireNonNull(time, "time must not be null");
    Objects.requireNonNull(clockStatus, "clockStatus must not be null");
    this.checkDeviation(deviation);
    this.date = new CosemDateDto(date);
    this.time = new CosemTimeDto(time);
    if (deviation == -DEVIATION_NOT_SPECIFIED) {
      /*
       * Has to do with specifics regarding 4 byte shorts and int values.
       * See comments with isDeviationNotSpecified(int).
       */
      this.deviation = DEVIATION_NOT_SPECIFIED;
    } else {
      this.deviation = deviation;
    }
    this.clockStatus = clockStatus;
  }

  public CosemDateTimeDto(final CosemDateTimeDto cosemDateTime) {
    this(
        cosemDateTime.getDate(),
        cosemDateTime.getTime(),
        cosemDateTime.getDeviation(),
        cosemDateTime.getClockStatus());
  }

  public CosemDateTimeDto(
      final LocalDate date,
      final LocalTime time,
      final int deviation,
      final ClockStatusDto clockStatus) {
    this(new CosemDateDto(date), new CosemTimeDto(time), deviation, clockStatus);
  }

  public CosemDateTimeDto(final LocalDate date, final LocalTime time, final int deviation) {
    this(
        new CosemDateDto(date),
        new CosemTimeDto(time),
        deviation,
        new ClockStatusDto(ClockStatusDto.STATUS_NOT_SPECIFIED));
  }

  public CosemDateTimeDto(
      final LocalDateTime dateTime, final int deviation, final ClockStatusDto clockStatus) {
    this(dateTime.toLocalDate(), dateTime.toLocalTime(), deviation, clockStatus);
  }

  public CosemDateTimeDto(final LocalDateTime dateTime, final int deviation) {
    this(dateTime.toLocalDate(), dateTime.toLocalTime(), deviation);
  }

  public CosemDateTimeDto(final ZonedDateTime dateTime) {
    this(
        dateTime.toLocalDate(),
        dateTime.toLocalTime(),
        determineDeviation(dateTime),
        determineClockStatus(dateTime));
  }

  public CosemDateTimeDto() {
    this(ZonedDateTime.now());
  }

  private void checkDeviation(final int deviation) {
    if (!this.isValidDeviation(deviation)) {
      throw new IllegalArgumentException("Deviation not in [-720..720, 0x8000]: " + deviation);
    }
  }

  private boolean isValidDeviation(final int deviation) {
    return this.isSpecificDeviation(deviation) || this.isDeviationNotSpecified(deviation);
  }

  private boolean isSpecificDeviation(final int deviation) {
    return deviation >= -720 && deviation <= 720;
  }

  private boolean isDeviationNotSpecified(final int deviation) {
    /*
     * Deviation comes from a short value (4 bytes), where the int value of
     * DEVIATION_NOT_SPECIFIED equals the negative value as 4 byte short.
     * Take this into account checking the deviation value.
     */
    return DEVIATION_NOT_SPECIFIED == deviation || -DEVIATION_NOT_SPECIFIED == deviation;
  }

  private static int determineDeviation(final ZonedDateTime dateTime) {
    return -(dateTime.getZone().getRules().getOffset(Instant.now()).getTotalSeconds()
        / MILLISECONDS_PER_SECOND);
  }

  private static ClockStatusDto determineClockStatus(final ZonedDateTime dateTime) {
    final Set<ClockStatusBitDto> statusBits = EnumSet.noneOf(ClockStatusBitDto.class);
    if (!dateTime.getZone().getRules().isDaylightSavings(dateTime.toInstant())) {
      statusBits.add(ClockStatusBitDto.DAYLIGHT_SAVING_ACTIVE);
    }
    return new ClockStatusDto(statusBits);
  }

  @Override
  public String toString() {
    return "CosemDateTime["
        + this.date
        + ' '
        + this.time
        + ", deviation="
        + this.deviation
        + ", "
        + this.clockStatus
        + ']';
  }

  public CosemDateDto getDate() {
    return this.date;
  }

  public CosemTimeDto getTime() {
    return this.time;
  }

  public int getDeviation() {
    return this.deviation;
  }

  public ClockStatusDto getClockStatus() {
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
   * Returns this {@link CosemDateTimeDto} as {@link ZonedDateTime} if the date, time and deviation
   * are specified.
   *
   * @return this {@link CosemDateTimeDto} as {@link ZonedDateTime}, or {@code null} if not {@link
   *     #isDateTimeSpecified()}.
   * @see #isDateTimeSpecified()
   */
  public ZonedDateTime asDateTime() {
    if (!this.isDateTimeSpecified()) {
      return null;
    }
    final LocalDateTime localDateTime = this.asLocalDateTime();
    return localDateTime.atZone(
        ZoneOffset.ofTotalSeconds(this.deviation * MILLISECONDS_PER_SECOND));
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
   * Returns this {@link CosemDateTimeDto} as {@link LocalDateTime} if the date and time are
   * specified.
   *
   * @return this {@link CosemDateTimeDto} as {@link LocalDateTime}, or {@code null} if not {@link
   *     #isLocalDateTimeSpecified()}.
   * @see #isLocalDateTimeSpecified()
   */
  public LocalDateTime asLocalDateTime() {
    if (!this.isLocalDateTimeSpecified()) {
      return null;
    }
    if (this.time.isSecondNotSpecified()) {
      return LocalDateTime.of(
          this.date.getYear(),
          this.date.getMonth(),
          this.date.getDayOfMonth(),
          this.time.getHour(),
          this.time.getMinute());
    }
    if (this.time.isHundredthsNotSpecified()) {
      return LocalDateTime.of(
          this.date.getYear(),
          this.date.getMonth(),
          this.date.getDayOfMonth(),
          this.time.getHour(),
          this.time.getMinute(),
          this.time.getSecond());
    }
    return LocalDateTime.of(
        this.date.getYear(),
        this.date.getMonth(),
        this.date.getDayOfMonth(),
        this.time.getHour(),
        this.time.getMinute(),
        this.time.getSecond(),
        this.time.getHundredths() * 10_000_000);
  }

  /**
   * @return {@code true} if the date is specified; {@code false} otherwise.
   * @see #getDate()
   * @see CosemDateDto#isLocalDateSpecified()
   */
  public boolean isLocalDateSpecified() {
    return this.date.isLocalDateSpecified();
  }

  /**
   * Returns this {@link CosemDateTimeDto} as {@link LocalDate} if the date is specified.
   *
   * @return this {@link CosemDateTimeDto} as {@link LocalDate}, or {@code null} if not {@link
   *     #isLocalDateSpecified()}.
   * @see #isLocalDateSpecified()
   */
  public LocalDate asLocalDate() {
    return this.date.asLocalDate();
  }

  /**
   * @return {@code true} if the time is specified; {@code false} otherwise.
   * @see #getTime()
   * @see CosemTimeDto#isLocalTimeSpecified()
   */
  public boolean isLocalTimeSpecified() {
    return this.time.isLocalTimeSpecified();
  }

  /**
   * Returns this {@link CosemDateTimeDto} as {@link LocalTime} if the time is specified.
   *
   * @return this {@link CosemDateTimeDto} as {@link LocalTime}, or {@code null} if not {@link
   *     #isLocalTimeSpecified()}.
   * @see #isLocalTimeSpecified()
   */
  public LocalTime asLocalTime() {
    return this.time.asLocalTime();
  }

  @Override
  public int compareTo(final CosemDateTimeDto o) {
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
    final CosemDateTimeDto other = (CosemDateTimeDto) obj;

    return Objects.equals(this.date, other.date)
        && Objects.equals(this.time, other.time)
        && Objects.equals(this.clockStatus, other.clockStatus)
        && this.deviation == other.deviation;
  }
}
