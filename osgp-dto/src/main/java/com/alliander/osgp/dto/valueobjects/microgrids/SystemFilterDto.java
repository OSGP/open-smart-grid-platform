package com.alliander.osgp.dto.valueobjects.microgrids;

import java.io.Serializable;
import java.util.List;

public class SystemFilterDto extends SystemIdentifierDto implements Serializable {

    private static final long serialVersionUID = 972589625016827390L;

    private List<MeasurementFilterDto> measurementFilters;
    private boolean all;

    public SystemFilterDto(final int id, final String systemType, final List<MeasurementFilterDto> measurementFilters,
            final boolean all) {
        super(id, systemType);
        this.measurementFilters = measurementFilters;
        this.all = all;
    }

    public List<MeasurementFilterDto> getMeasurementFilters() {
        return this.measurementFilters;
    }

    public void setMeasurementFilters(final List<MeasurementFilterDto> measurementFilters) {
        this.measurementFilters = measurementFilters;
    }

    public boolean isAll() {
        return this.all;
    }

    public void setAll(final boolean all) {
        this.all = all;
    }
}
