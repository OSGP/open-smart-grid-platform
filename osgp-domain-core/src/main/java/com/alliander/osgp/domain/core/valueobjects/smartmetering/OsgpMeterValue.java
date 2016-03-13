package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.math.BigDecimal;

/**
 * A metervalue with its standardized osgp unit
 *
 *
 */
public class OsgpMeterValue {

    public OsgpMeterValue(final BigDecimal value, final OsgpUnit osgpUnit) {
        super();
        this.value = value;
        this.osgpUnit = osgpUnit;
    }

    private final BigDecimal value;
    private final OsgpUnit osgpUnit;

    public BigDecimal getValue() {
        return this.value;
    }

    public OsgpUnit getDlmsUnit() {
        return this.osgpUnit;
    }

    @Override
    public String toString() {
        return "OsgpMeterValue [value=" + this.value + ", osgpUnit=" + this.osgpUnit + "]";
    }

}
