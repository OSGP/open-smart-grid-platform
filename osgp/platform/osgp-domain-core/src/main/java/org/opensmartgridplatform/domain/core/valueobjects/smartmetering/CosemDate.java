// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.time.LocalDate;

public class CosemDate implements Serializable, Comparable<CosemDate> {

  private static final long serialVersionUID = -3965207413972253042L;

  public static final int YEAR_NOT_SPECIFIED = 0xFFFF;

  public static final int MONTH_DAYLIGHT_SAVINGS_END = 0xFD;
  public static final int MONTH_DAYLIGHT_SAVINGS_BEGIN = 0xFE;
  public static final int MONTH_NOT_SPECIFIED = 0xFF;

  public static final int DAY_OF_MONTH_2ND_LAST_DAY = 0xFD;
  public static final int DAY_OF_MONTH_LAST_DAY = 0xFE;
  public static final int DAY_OF_MONTH_NOT_SPECIFIED = 0xFF;
  public static final int DAY_OF_MONTH_RESERVED_MIN = 0xE0;
  public static final int DAY_OF_MONTH_RESERVED_MAX = 0xFC;

  public static final int DAY_OF_WEEK_NOT_SPECIFIED = 0xFF;

  private final int year;
  private final int month;
  private final int dayOfMonth;
  private final int dayOfWeek;

  public CosemDate(final int year, final int month, final int dayOfMonth, final int dayOfWeek) {
    this.checkInputs(year, month, dayOfMonth, dayOfWeek);
    this.year = year;
    this.month = month;
    this.dayOfMonth = dayOfMonth;
    this.dayOfWeek = dayOfWeek;
  }

  public CosemDate(final int year, final int month, final int dayOfMonth) {
    this(year, month, dayOfMonth, DAY_OF_WEEK_NOT_SPECIFIED);
  }

  public CosemDate(final LocalDate date) {
    this(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), DAY_OF_WEEK_NOT_SPECIFIED);
  }

  public CosemDate(final CosemDate cosemDate) {
    this(
        cosemDate.getYear(),
        cosemDate.getMonth(),
        cosemDate.getDayOfMonth(),
        cosemDate.getDayOfWeek());
  }

  public CosemDate() {
    this(LocalDate.now());
  }

  private void checkInputs(
      final int year, final int month, final int dayOfMonth, final int dayOfWeek) {
    this.checkYear(year);
    this.checkMonth(month);
    this.checkDayOfMonth(dayOfMonth);
    this.checkDayOfWeek(dayOfWeek);
  }

  private void checkYear(final int year) {
    if (year < 0 || year > 0xFFFF) {
      throw new IllegalArgumentException("Year not in [0..65535]: " + year);
    }
  }

  private void checkMonth(final int month) {
    if (!this.isValidMonth(month)) {
      throw new IllegalArgumentException("Month not in [1..12, 0xFD, 0xFE, 0xFF]: " + month);
    }
  }

  private boolean isValidMonth(final int month) {
    return this.isSpecificMonth(month) || this.isSpecialMonth(month);
  }

  private boolean isSpecificMonth(final int month) {
    return month >= 1 && month <= 12;
  }

  private boolean isSpecialMonth(final int month) {
    return MONTH_DAYLIGHT_SAVINGS_END == month
        || MONTH_DAYLIGHT_SAVINGS_BEGIN == month
        || MONTH_NOT_SPECIFIED == month;
  }

  private void checkDayOfMonth(final int dayOfMonth) {
    if (!this.isValidDayOfMonth(dayOfMonth)) {
      throw new IllegalArgumentException("DayOfMonth not in [1..31, 0xE0..0xFF]: " + dayOfMonth);
    }
  }

  private boolean isValidDayOfMonth(final int dayOfMonth) {
    return this.isSpecificDayOfMonth(dayOfMonth) || this.isSpecialDayOfMonth(dayOfMonth);
  }

  private boolean isSpecificDayOfMonth(final int dayOfMonth) {
    return dayOfMonth >= 1 && dayOfMonth <= 31;
  }

  private boolean isSpecialDayOfMonth(final int dayOfMonth) {
    return DAY_OF_MONTH_2ND_LAST_DAY == dayOfMonth
        || DAY_OF_MONTH_LAST_DAY == dayOfMonth
        || DAY_OF_MONTH_NOT_SPECIFIED == dayOfMonth
        || this.isReservedDayOfMonth(dayOfMonth);
  }

  private boolean isReservedDayOfMonth(final int dayOfMonth) {
    return dayOfMonth >= DAY_OF_MONTH_RESERVED_MIN && dayOfMonth <= DAY_OF_MONTH_RESERVED_MAX;
  }

  private void checkDayOfWeek(final int dayOfWeek) {
    if (!this.isValidDayOfWeek(dayOfWeek)) {
      throw new IllegalArgumentException("DayOfWeek not in [1..7, 0xFF]: " + dayOfWeek);
    }
  }

  private boolean isValidDayOfWeek(final int dayOfWeek) {
    return this.isSpecificDayOfWeek(dayOfWeek) || DAY_OF_WEEK_NOT_SPECIFIED == dayOfWeek;
  }

  private boolean isSpecificDayOfWeek(final int dayOfWeek) {
    return dayOfWeek >= 1 && dayOfWeek <= 7;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    if (!this.isDayOfWeekNotSpecified()) {
      this.appendDayOfWeek(sb);
      sb.append(' ');
    }
    this.appendYear(sb);
    this.appendMonth(sb);
    this.appendDayOfMonth(sb);
    this.appendDayOfWeek(sb);
    return sb.toString();
  }

  private void appendYear(final StringBuilder sb) {
    if (this.isYearNotSpecified()) {
      sb.append(String.format("%04X", this.year));
    } else {
      sb.append(String.format("%04d", this.year));
    }
  }

  private void appendMonth(final StringBuilder sb) {
    if (this.isSpecialMonth(this.month)) {
      sb.append(String.format("%02X", this.month));
    } else {
      sb.append(String.format("%02d", this.month));
    }
  }

  private void appendDayOfMonth(final StringBuilder sb) {
    if (this.isSpecialDayOfMonth(this.dayOfMonth)) {
      sb.append(String.format("%02X", this.dayOfMonth));
    } else {
      sb.append(String.format("%02d", this.dayOfMonth));
    }
  }

  private void appendDayOfWeek(final StringBuilder sb) {
    switch (this.dayOfWeek) {
      case 1:
        sb.append("Monday");
        break;
      case 2:
        sb.append("Tuesday");
        break;
      case 3:
        sb.append("Wednesday");
        break;
      case 4:
        sb.append("Thursday");
        break;
      case 5:
        sb.append("Friday");
        break;
      case 6:
        sb.append("Saturday");
        break;
      case 7:
        sb.append("Sunday");
        break;
      case 0xFF:
        sb.append("Day of week not specified");
        break;
      default:
        throw new AssertionError("DayOfWeek value unknown: " + this.dayOfWeek);
    }
  }

  /**
   * @return the year for this {@link CosemDate}.
   * @see #YEAR_NOT_SPECIFIED
   */
  public int getYear() {
    return this.year;
  }

  /**
   * @return the month for this {@link CosemDate} (January=1 .. December=12).
   * @see #MONTH_DAYLIGHT_SAVINGS_END
   * @see #MONTH_DAYLIGHT_SAVINGS_BEGIN
   * @see #MONTH_NOT_SPECIFIED
   */
  public int getMonth() {
    return this.month;
  }

  /**
   * @return the day of month for this {@link CosemDate} (1..31).
   * @see #DAY_OF_MONTH_2ND_LAST_DAY
   * @see #DAY_OF_MONTH_LAST_DAY
   * @see #DAY_OF_MONTH_NOT_SPECIFIED
   * @see #DAY_OF_MONTH_RESERVED_MIN
   * @see #DAY_OF_MONTH_RESERVED_MAX
   */
  public int getDayOfMonth() {
    return this.dayOfMonth;
  }

  /**
   * @return the day of the week for this {@link CosemDate} (Monday=1 .. Sunday=7).
   * @see #DAY_OF_WEEK_NOT_SPECIFIED
   */
  public int getDayOfWeek() {
    return this.dayOfWeek;
  }

  /**
   * @return {@code true} if the values for {@code year}, {@code month} and {@code dayOfMonth}
   *     contain values for regular dates; {@code false} if wildcards are used in the fields
   *     mentioned.
   */
  public boolean isLocalDateSpecified() {
    return YEAR_NOT_SPECIFIED != this.year
        && this.isSpecificMonth(this.month)
        && this.isSpecificDayOfMonth(this.dayOfMonth);
  }

  /**
   * Returns this {@link CosemDate} as {@link LocalDate} if {@code year}, {@code month} and {@code
   * dayOfMonth} do not contain wildcard values.
   *
   * @return this {@link CosemDate} as {@link LocalDate}, or {@code null} if not {@link
   *     #isLocalDateSpecified()}.
   * @see #isLocalDateSpecified()
   */
  public LocalDate asLocalDate() {
    if (this.isLocalDateSpecified()) {
      return LocalDate.of(this.year, this.month, this.dayOfMonth);
    }
    return null;
  }

  public boolean isYearNotSpecified() {
    return YEAR_NOT_SPECIFIED == this.year;
  }

  public boolean isMonthForDaylightSavingsEnd() {
    return MONTH_DAYLIGHT_SAVINGS_END == this.month;
  }

  public boolean isMonthForDaylightSavingsBegin() {
    return MONTH_DAYLIGHT_SAVINGS_BEGIN == this.month;
  }

  public boolean isMonthNotSpecified() {
    return MONTH_NOT_SPECIFIED == this.month;
  }

  public boolean isDayOfMonthNotSpecified() {
    return DAY_OF_MONTH_NOT_SPECIFIED == this.dayOfMonth;
  }

  public boolean isSecondLastDayOfMonth() {
    return DAY_OF_MONTH_2ND_LAST_DAY == this.dayOfMonth;
  }

  public boolean isLastDayOfMonth() {
    return DAY_OF_MONTH_LAST_DAY == this.dayOfMonth;
  }

  public boolean isReservedDayOfMonth() {
    return this.isReservedDayOfMonth(this.dayOfMonth);
  }

  public boolean isDayOfWeekNotSpecified() {
    return DAY_OF_WEEK_NOT_SPECIFIED == this.dayOfWeek;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.dayOfMonth;
    result = prime * result + this.dayOfWeek;
    result = prime * result + this.month;
    result = prime * result + this.year;
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
    final CosemDate other = (CosemDate) obj;

    return this.dayOfMonth == other.dayOfMonth
        && this.dayOfWeek == other.dayOfWeek
        && this.month == other.month
        && this.year == other.year;
  }

  @Override
  public int compareTo(final CosemDate o) {
    if (this.compareNotEqual(this.year, o.year, YEAR_NOT_SPECIFIED)) {
      return this.year - o.year;
    }
    if (this.compareNotEqual(this.month, o.month, MONTH_NOT_SPECIFIED)) {
      return this.month - o.month;
    }
    if (this.compareNotEqual(this.dayOfMonth, o.dayOfMonth, DAY_OF_MONTH_NOT_SPECIFIED)) {
      return this.dayOfMonth - o.dayOfMonth;
    }
    if (this.compareNotEqual(this.dayOfWeek, o.dayOfWeek, DAY_OF_WEEK_NOT_SPECIFIED)) {
      return this.dayOfWeek - o.dayOfWeek;
    }

    return 0;
  }

  private boolean compareNotEqual(
      final int value, final int compareValue, final int unspecifiedConstant) {
    return value != unspecifiedConstant
        && compareValue != unspecifiedConstant
        && value - compareValue != 0;
  }
}
