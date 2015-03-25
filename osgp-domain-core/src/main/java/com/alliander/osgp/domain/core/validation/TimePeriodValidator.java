package com.alliander.osgp.domain.core.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.alliander.osgp.domain.core.valueobjects.TimePeriod;

public class TimePeriodValidator implements ConstraintValidator<TimePeriodConstraints, TimePeriod> {

    private static final String CHECK_STARTTIME_MESSAGE = "startTime may not be after endTime";

    @Override
    public void initialize(final TimePeriodConstraints constraintAnnotation) {
        // Empty Method
    }

    @Override
    public boolean isValid(final TimePeriod timePeriod, final ConstraintValidatorContext context) {
        if (timePeriod == null) {
            return true;
        }

        final ValidatorHelper helper = new ValidatorHelper();

        if (timePeriod.getStartTime().isAfter(timePeriod.getEndTime())) {
            helper.addMessage(CHECK_STARTTIME_MESSAGE);
        }

        return helper.isValid(context);
    }
}
