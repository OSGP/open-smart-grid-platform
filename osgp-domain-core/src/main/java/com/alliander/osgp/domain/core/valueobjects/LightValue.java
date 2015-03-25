package com.alliander.osgp.domain.core.valueobjects;

import java.io.Serializable;

import org.hibernate.validator.constraints.Range;

import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.domain.core.validation.LightValueConstraints;

@LightValueConstraints
public class LightValue implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2244245336355424779L;
    private static final int MIN_INDEX = 0;
    private static final int MAX_INDEX = 6;

    private static final int MIN_DIMVALUE = 1;
    private static final int MAX_DIMVALUE = 100;

    @Range(min = MIN_INDEX, max = MAX_INDEX)
    private final Integer index;

    private boolean on;

    @Range(min = MIN_DIMVALUE, max = MAX_DIMVALUE)
    private final Integer dimValue;

    public LightValue(final Integer index, final boolean on, final Integer dimValue) throws ValidationException {
        this.index = index;
        this.on = on;
        this.dimValue = dimValue;
    }

    public Integer getIndex() {
        return this.index;
    }

    public boolean isOn() {
        return this.on;
    }

    public Integer getDimValue() {
        return this.dimValue;
    }

    public void invertIsOn() {
        this.on = !this.on;
    }
}
