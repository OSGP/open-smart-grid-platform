// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem.processing;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.openmuc.jdlms.datatypes.CosemDateFormat.Field;
import org.openmuc.jdlms.datatypes.CosemDateTime;
import org.openmuc.jdlms.datatypes.DataObject;

class CosemDateTimeFilter extends RangeDescriptorFilter {

  private static final int LAST_DAY_OF_MONTH = 0xfe;
  private static final int SECOND_LAST_DAY_OF_MONTH = 0xfd;
  private static final int NOT_SPECIFIED = 0xff;

  private final Calendar from;

  private final Calendar to;

  CosemDateTimeFilter(final List<DataObject> rangeDescriptor) {
    super(rangeDescriptor);
    this.from = this.toCalendar(CosemDateTime.decode(rangeDescriptor.get(1).getValue()));
    this.to = this.toCalendar(CosemDateTime.decode(rangeDescriptor.get(2).getValue()));
  }

  @Override
  public boolean match(final Object match) {
    if (!(match instanceof Calendar)) {
      throw new IllegalArgumentException(
          this.getClass().getSimpleName()
              + " does not support matching of type "
              + match.getClass().getSimpleName());
    }

    final Calendar cal = (Calendar) match;
    return this.from.before(cal) && this.to.after(cal);
  }

  public Calendar toCalendar(final CosemDateTime dateTimeValue) {

    final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    final int year = dateTimeValue.get(Field.YEAR);
    final int month = dateTimeValue.get(Field.MONTH);
    int dayOfMonth = dateTimeValue.get(Field.DAY_OF_MONTH);
    final int dayOfWeek = dateTimeValue.get(Field.DAY_OF_WEEK);
    final int hour = dateTimeValue.get(Field.HOUR);
    final int minute = dateTimeValue.get(Field.MINUTE);
    final int second = dateTimeValue.get(Field.SECOND);
    final int hundredths = dateTimeValue.get(Field.HUNDREDTHS);

    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month - 1);
    if (dayOfMonth == LAST_DAY_OF_MONTH) {
      if (dayOfWeek == NOT_SPECIFIED) {
        this.dayOfMonthToLastDay(calendar);
      } else {
        this.lastWeekDayInMonth(calendar, dayOfWeek);
      }
    } else if (dayOfMonth == SECOND_LAST_DAY_OF_MONTH) {
      dayOfMonth = this.lastDayOfMonth(calendar) - 1;
      calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    } else {
      calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }

    if (hour != NOT_SPECIFIED) {
      calendar.set(Calendar.HOUR_OF_DAY, hour);
    }

    if (minute != NOT_SPECIFIED) {
      calendar.set(Calendar.MINUTE, minute);
    }

    if (second != NOT_SPECIFIED) {
      calendar.set(Calendar.SECOND, second);
    }

    if (hundredths != NOT_SPECIFIED) {
      calendar.set(Calendar.MILLISECOND, hundredths * 10);
    }

    final int deviation = dateTimeValue.get(Field.DEVIATION);

    if (((deviation & 0xFFFF) ^ 0x8000) != 0x0) {
      final int timeZoneOffset =
          (int) TimeUnit.MILLISECONDS.convert(deviation, TimeUnit.MINUTES) * -1;
      calendar.set(Calendar.ZONE_OFFSET, timeZoneOffset);
    }

    return calendar;
  }

  private void dayOfMonthToLastDay(final Calendar calendar) {
    final int dayOfMonth;
    dayOfMonth = this.lastDayOfMonth(calendar);
    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
  }

  private int lastDayOfMonth(final Calendar calendar) {
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
  }

  private void lastWeekDayInMonth(final Calendar calendar, final int dayOfWeek) {
    calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek + 1);
    calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, -1);
  }
}
