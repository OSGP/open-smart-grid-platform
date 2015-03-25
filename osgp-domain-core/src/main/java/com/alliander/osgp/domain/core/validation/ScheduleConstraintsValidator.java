package com.alliander.osgp.domain.core.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.alliander.osgp.domain.core.valueobjects.ActionTimeType;
import com.alliander.osgp.domain.core.valueobjects.Schedule;
import com.alliander.osgp.domain.core.valueobjects.WeekDayType;

public class ScheduleConstraintsValidator implements ConstraintValidator<ScheduleConstraints, Schedule> {

    private static final String CHECK_START_DAY_MESSAGE = "startDay may not be null when weekDay is set to ABSOLUTEDAY";
    private static final String CHECK_START_DAY_AFTER_END_DAY_MESSAGE = "startDay may not be later than endDay when weekDay is set to ABSOLUTEDAY";
    private static final String CHECK_TIME_MESSAGE = "time may not be null when actionTime is set to ABSOLUTETIME";
    private static final String CHECK_TRIGGER_WINDOW_MESSAGE = "triggerWindow may not be null when actionTime is set to SUNRISE or SUNSET";
    private static final String CHECK_TRIGGER_WINDOW_MINUTES_BEFORE_MESSAGE = "triggerWindow minutesBefore must be between 0 and 1440 minutes";
    private static final String CHECK_TRIGGER_WINDOW_MINUTES_AFTER_MESSAGE = "triggerWindow minutesAfter must be between 0 and 1440 minutes";

    @Override
    public void initialize(final ScheduleConstraints constraintAnnotation) {
        // Empty Method
    }

    @Override
    public boolean isValid(final Schedule schedule, final ConstraintValidatorContext context) {
        final ValidatorHelper helper = new ValidatorHelper();
        this.checkStartDay(helper, schedule);
        this.checkTime(helper, schedule);
        this.checkTriggerWindow(helper, schedule);

        return helper.isValid(context);
    }

    private void checkStartDay(final ValidatorHelper helper, final Schedule schedule) {
        if (schedule.getWeekDay() == WeekDayType.ABSOLUTEDAY) {
            // When weekDay is ABSOLUTEDAY then startDay may not be null
            if (schedule.getStartDay() == null) {
                helper.addMessage(CHECK_START_DAY_MESSAGE);
            }

            if (schedule.getEndDay() != null) {
                // When weekDay is ABSOLUTEDAY then startDay may not be later
                // than endDay
                if (schedule.getStartDay() != null && schedule.getStartDay().isAfter(schedule.getEndDay())) {
                    helper.addMessage(CHECK_START_DAY_AFTER_END_DAY_MESSAGE);
                }
            }
        }
    }

    private void checkTime(final ValidatorHelper helper, final Schedule schedule) {
        if (schedule.getActionTime() == ActionTimeType.ABSOLUTETIME) {
            // When actionTime is ABSOLUTETIME then time may not be null
            if (schedule.getTime() == null) {
                helper.addMessage(CHECK_TIME_MESSAGE);
            }
        }
    }

    private void checkTriggerWindow(final ValidatorHelper helper, final Schedule schedule) {
        if ((schedule.getActionTime() == ActionTimeType.SUNRISE || schedule.getActionTime() == ActionTimeType.SUNSET)) {
            // When actionTime is SUNRISE or SUNSET then triggerWindow may not
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
