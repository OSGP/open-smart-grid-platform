package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.math.BigDecimal;

/**
 * A metervalue with scaler applied together with its unit on the meter
 *
 * @author dev
 *
 */
public class DlmsMeterValue {

    public DlmsMeterValue(final BigDecimal value, final DlmsUnit dlmsUnit) {
        super();
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
