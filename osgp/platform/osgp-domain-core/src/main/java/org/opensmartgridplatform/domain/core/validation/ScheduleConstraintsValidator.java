/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.opensmartgridplatform.domain.core.valueobjects.ActionTimeType;
import org.opensmartgridplatform.domain.core.valueobjects.ScheduleEntry;
import org.opensmartgridplatform.domain.core.valueobjects.TriggerType;
import org.opensmartgridplatform.domain.core.valueobjects.WeekDayType;

public class ScheduleConstraintsValidator
    implements ConstraintValidator<ScheduleConstraints, ScheduleEntry> {

  private static final String CHECK_START_DAY_MESSAGE =
      "startDay may not be null when weekDay is set to ABSOLUTEDAY";
  private static final String CHECK_START_DAY_AFTER_END_DAY_MESSAGE =
      "startDay may not be later than endDay when weekDay is set to ABSOLUTEDAY";
  private static final String CHECK_TIME_MESSAGE =
      "time may not be null when actionTime is set to ABSOLUTETIME";
  private static final String CHECK_TRIGGER_WINDOW_MESSAGE =
      "triggerWindow may not be null when actionTime is set to SUNRISE or SUNSET and triggerType is LIGHT_TRIGGER";
  private static final String CHECK_TRIGGER_WINDOW_MINUTES_BEFORE_MESSAGE =
      "triggerWindow minutesBefore must be between 0 and 1440 minutes";
  private static final String CHECK_TRIGGER_WINDOW_MINUTES_AFTER_MESSAGE =
      "triggerWindow minutesAfter must be between 0 and 1440 minutes";

  @Override
  public void initialize(final ScheduleConstraints constraintAnnotation) {
    // Empty Method
  }

  @Override
  public boolean isValid(final ScheduleEntry schedule, final ConstraintValidatorContext context) {
    final ValidatorHelper helper = new ValidatorHelper();
    this.checkStartDay(helper, schedule);
    this.checkTime(helper, schedule);
    this.checkTriggerWindow(helper, schedule);

    return helper.isValid(context);
  }

  private void checkStartDay(final ValidatorHelper helper, final ScheduleEntry schedule) {
    if (schedule.getWeekDay() == WeekDayType.ABSOLUTEDAY) {
      // When weekDay is ABSOLUTEDAY then startDay may not be null
      if (schedule.getStartDay() == null) {
        helper.addMessage(CHECK_START_DAY_MESSAGE);
      }

      // When weekDay is ABSOLUTEDAY then startDay may not be later
      // than endDay
      if (schedule.getEndDay() != null
          && schedule.getStartDay() != null
          && schedule.getStartDay().isAfter(schedule.getEndDay())) {
        helper.addMessage(CHECK_START_DAY_AFTER_END_DAY_MESSAGE);
      }
    }
  }

  private void checkTime(final ValidatorHelper helper, final ScheduleEntry schedule) {
    // When actionTime is ABSOLUTETIME then time may not be null
    if (schedule.getActionTime() == ActionTimeType.ABSOLUTETIME && schedule.getTime() == null) {
      helper.addMessage(CHECK_TIME_MESSAGE);
    }
  }

  private void checkTriggerWindow(final ValidatorHelper helper, final ScheduleEntry schedule) {
    if ((ActionTimeType.SUNRISE.equals(schedule.getActionTime())
            || ActionTimeType.SUNSET.equals(schedule.getActionTime()))
        && TriggerType.LIGHT_TRIGGER.equals(schedule.getTriggerType())) {
      // When actionTime is SUNRISE or SUNSET and triggertype is
      // LIGHT_TRIGGER then triggerWindow may not
      // be null
      if (schedule.getTriggerWindow() == null) {
        helper.addMessage(CHECK_TRIGGER_WINDOW_MESSAGE);
      } else {
        // Check that minutes before is between 0 and 1440 minutes (1
        // day)
        if (schedule.getTriggerWindow().getMinutesBefore() < 0
            || schedule.getTriggerWindow().getMinutesBefore() > 1440) {
          helper.addMessage(CHECK_TRIGGER_WINDOW_MINUTES_BEFORE_MESSAGE);
        }
        // Check that minutes after is between 0 and 1440 minutes (1
        // day)
        if (schedule.getTriggerWindow().getMinutesAfter() < 0
            || schedule.getTriggerWindow().getMinutesAfter() > 1440) {
          helper.addMessage(CHECK_TRIGGER_WINDOW_MINUTES_AFTER_MESSAGE);
        }
      }
    }
  }
}
