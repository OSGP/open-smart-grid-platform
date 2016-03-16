package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A metervalue with scaler applied together with its unit on the meter
 *
 *
 */
public class DlmsMeterValue implements Serializable {

    private static final long serialVersionUID = 1L;

    public DlmsMeterValue(final BigDecimal value, final DlmsUnit dlmsUnit) {
        this.value = value;
        this.dlmsUnit = dlmsUnit;
    }

    private final BigDecimal value;
    private final DlmsUnit dlmsUnit;

    public BigDecimal getValue() {
        return this.value;
    }

    public DlmsUnit getDlmsUnit() {
        return this.dlmsUnit;
    }

    @Override
    public String toString() {
        return "DlmsMeterValue [value=" + this.value + ", dlmsUnit=" + this.dlmsUnit + "]";
    }

}
