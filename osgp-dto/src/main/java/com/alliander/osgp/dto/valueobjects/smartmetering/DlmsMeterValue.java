package com.alliander.osgp.dto.valueobjects.smartmetering;

public class DlmsMeterValue {

    public DlmsMeterValue(final Long value, final ScalerUnit scalerUnit) {
        super();
        this.value = value;
        this.scalerUnit = scalerUnit;
    }

    private final Long value;
    private final ScalerUnit scalerUnit;

    public Long getValue() {
        return this.value;
    }

    public ScalerUnit getScalerUnit() {
        return this.scalerUnit;
    }

    @Override
    public String toString() {
        return "DlmsMeterValue [value=" + this.value + ", scalerUnit=" + this.scalerUnit + "]";
    }

}
