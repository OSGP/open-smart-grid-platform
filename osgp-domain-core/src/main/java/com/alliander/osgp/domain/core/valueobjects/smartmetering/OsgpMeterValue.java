package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A metervalue with its standardized osgp unit
 *
 *
 */
public class OsgpMeterValue implements Serializable {

    private static final long serialVersionUID = 1L;

    public OsgpMeterValue(final BigDecimal value, final OsgpUnit osgpUnit) {
        this.value = value;
        this.osgpUnit = osgpUnit;
    }

    private final BigDecimal value;
    private final OsgpUnit osgpUnit;

    public BigDecimal getValue() {
        return this.value;
    }

    public OsgpUnit getOsgpUnit() {
        return this.osgpUnit;
    }

    @Override
    public String toString() {
        return "OsgpMeterValue [value=" + this.value + ", osgpUnit=" + this.osgpUnit + "]";
    }

}
