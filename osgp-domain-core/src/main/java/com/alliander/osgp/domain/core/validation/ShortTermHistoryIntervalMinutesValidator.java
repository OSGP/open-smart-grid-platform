package com.alliander.osgp.domain.core.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.alliander.osgp.domain.core.valueobjects.Configuration;

public class ShortTermHistoryIntervalMinutesValidator implements
        ConstraintValidator<ShortTermHistoryIntervalMinutes, Configuration> {

    private final List<Integer> values = new ArrayList<Integer>();

    @Override
    public void initialize(final ShortTermHistoryIntervalMinutes arg0) {
        // Permitted values are null (not set), 15 minutes, 30 minutes, 60
        // minutes, 240 minutes.
        this.values.add(null);
        this.values.add(15);
        this.values.add(30);
        this.values.add(60);
        this.values.add(240);
    }

    @Override
    public boolean isValid(final Configuration value, final ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return this.values.contains(value.getShortTermHistoryIntervalMinutes());
    }
}
