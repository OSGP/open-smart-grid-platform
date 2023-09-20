// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.temporal.ChronoField;

public class CosemTimeDto implements Serializable, Comparable<CosemTimeDto> {

  private static final long serialVersionUID = 1505799304987059469L;

  public static final int HOUR_NOT_SPECIFIED = 0xFF;
  public static final int MINUTE_NOT_SPECIFIED = 0xFF;
  public static final int SECOND_NOT_SPECIFIED = 0xFF;
  public static final int HUNDREDTHS_NOT_SPECIFIED = 0xFF;

  private final int hour;
  private final int minute;
  private final int second;
  private final int hundredths;

  public CosemTimeDto(final int hour, final int minute, final int second, final int hundredths) {
    this.checkInputs(hour, minute, second, hundredths);
    this.hour = hour;
    this.minute = minute;
    this.second = second;
    this.hundredths = hundredths;
  }

  public CosemTimeDto(final int hour, final int minute, final int second) {
    this(hour, minute, second, HUNDREDTHS_NOT_SPECIFIED);
  }

  public CosemTimeDto(final int hour, final int minute) {
    this(hour, minute, SECOND_NOT_SPECIFIED, HUNDREDTHS_NOT_SPECIFIED);
  }

  public CosemTimeDto(final LocalTime time) {
    this(
        time.getHour(),
        time.getMinute(),
        time.getSecond(),
        time.get(ChronoField.MILLI_OF_SECOND) / 10);
  }

  public CosemTimeDto(final CosemTimeDto time) {
    this(time.getHour(), time.getMinute(), time.getSecond(), time.getHundredths());
  }

  public CosemTimeDto() {
    this(LocalTime.now());
  }

  private void checkInputs(
      final int hour, final int minute, final int second, final int hundredths) {
    this.checkHour(hour);
    this.checkMinute(minute);
    this.checkSecond(second);
    this.checkHundredths(hundredths);
  }

  private void checkHour(final int hour) {
    if (hour < 0 || hour > 23 && HOUR_NOT_SPECIFIED != hour) {
      throw new IllegalArgumentException("Hour not in [0..23, 0xFF]: " + hour);
    }
  }

  private void checkMinute(final int minute) {
    if (minute < 0 || minute > 59 && MINUTE_NOT_SPECIFIED != minute) {
      throw new IllegalArgumentException("Minute not in [0..59, 0xFF]: " + minute);
    }
  }

  private void checkSecond(final int second) {
    if (second < 0 || second > 59 && SECOND_NOT_SPECIFIED != second) {
      throw new IllegalArgumentException("Second not in [0..59, 0xFF]: " + second);
    }
  }

  private void checkHundredths(final int hundredths) {
    if (hundredths < 0 || hundredths > 99 && HUNDREDTHS_NOT_SPECIFIED != hundredths) {
      throw new IllegalArgumentException("Hundredths not in [0..99, 0xFF]: " + hundredths);
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    this.appendHour(sb);
    sb.append(':');
    this.appendMinute(sb);
    if (SECOND_NOT_SPECIFIED == this.second && HUNDREDTHS_NOT_SPECIFIED == this.hundredths) {
      return sb.toString();
    }
    sb.append(':');
    this.appendSecond(sb);
    if (HUNDREDTHS_NOT_SPECIFIED == this.hundredths) {
      return sb.toString();
    }
    sb.append('.');
    this.appendHundredths(sb);
    return sb.toString();
  }

  private void appendHour(final StringBuilder sb) {
    if (this.isHourNotSpecified()) {
      sb.append(String.format("%02X", this.hour));
    } else {
      sb.append(String.format("%02d", this.hour));
    }
  }

  private void appendMinute(final StringBuilder sb) {
    if (this.isMinuteNotSpecified()) {
      sb.append(String.format("%02X", this.minute));
    } else {
      sb.append(String.format("%02d", this.minute));
    }
  }

  private void appendSecond(final StringBuilder sb) {
    if (this.isSecondNotSpecified()) {
      sb.append(String.format("%02X", this.second));
    } else {
      sb.append(String.format("%02d", this.second));
    }
  }

  private void appendHundredths(final StringBuilder sb) {
    if (this.isHundredthsNotSpecified()) {
      sb.append(String.format("%02X", this.hundredths));
    } else {
      sb.append(String.format("%02d", this.hundredths));
    }
  }

  /**
   * @return the hour for this {@link CosemTimeDto}.
   * @see #HOUR_NOT_SPECIFIED
   */
  public int getHour() {
    return this.hour;
  }

  /**
   * @return the minute for this {@link CosemTimeDto}.
   * @see #MINUTE_NOT_SPECIFIED
   */
  public int getMinute() {
    return this.minute;
  }

  /**
   * @return the second for this {@link CosemTimeDto}.
   * @see #SECOND_NOT_SPECIFIED
   */
  public int getSecond() {
    return this.second;
  }

  /**
   * @return the hundredths for this {@link CosemTimeDto}.
   * @see #HUNDREDTHS_NOT_SPECIFIED
   */
  public int getHundredths() {
    return this.hundredths;
  }

  /**
   * @return {@code true} if at least the values for {@code hour} and {@code minute} contain values
   *     for regular times; {@code false} if wildcards are used in the fields mentioned.
   */
  public boolean isLocalTimeSpecified() {
    return HOUR_NOT_SPECIFIED != this.hour && MINUTE_NOT_SPECIFIED != this.minute;
  }

  /**
   * Returns this {@link CosemTimeDto} as {@link LocalTime} if {@code hour}, {@code minute} do not
   * contain wildcard values.
   *
   * @return this {@link CosemTimeDto} as {@link LocalTime}, or {@code null} if not {@link
   *     #isLocalTimeSpecified()}.
   * @see #isLocalTimeSpecified()
   */
  public LocalTime asLocalTime() {
    if (!this.isLocalTimeSpecified()) {
      return null;
    }
    if (SECOND_NOT_SPECIFIED == this.second) {
      return LocalTime.of(this.hour, this.minute);
    }
    if (HUNDREDTHS_NOT_SPECIFIED == this.hundredths) {
      return LocalTime.of(this.hour, this.minute, this.second);
    }
    return LocalTime.of(this.hour, this.minute, this.second, this.hundredths * 10_000_000);
  }

  public boolean isHourNotSpecified() {
    return HOUR_NOT_SPECIFIED == this.hour;
  }

  public boolean isMinuteNotSpecified() {
    return MINUTE_NOT_SPECIFIED == this.minute;
  }

  public boolean isSecondNotSpecified() {
    return SECOND_NOT_SPECIFIED == this.second;
  }

  public boolean isHundredthsNotSpecified() {
    return HUNDREDTHS_NOT_SPECIFIED == this.hundredths;
  }

  @Override
  public int compareTo(final CosemTimeDto o) {
    // NOT_SPECIFIED equals every other value.
    if (this.compareNotEqual(this.hour, o.hour, HOUR_NOT_SPECIFIED)) {
      return this.hour - o.hour;
    }

    if (this.compareNotEqual(this.minute, o.minute, MINUTE_NOT_SPECIFIED)) {
      return this.minute - o.minute;
    }

    if (this.compareNotEqual(this.second, o.second, SECOND_NOT_SPECIFIED)) {
      return this.second - o.second;
    }

    if (this.compareNotEqual(this.hundredths, o.hundredths, HUNDREDTHS_NOT_SPECIFIED)) {
      return this.hundredths - o.hundredths;
    }

    return 0;
  }

  private boolean compareNotEqual(
      final int value, final int compareValue, final int unspecifiedConstant) {
    return value != unspecifiedConstant
        && compareValue != unspecifiedConstant
        && value - compareValue != 0;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.hour;
    result = prime * result + this.hundredths;
    result = prime * result + this.minute;
    result = prime * result + this.second;
    return result;
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
    final CosemTimeDto other = (CosemTimeDto) obj;

    return this.hundredths == other.hundredths
        && this.second == other.second
        && this.minute == other.minute
        && this.hour == other.hour;
  }
}
