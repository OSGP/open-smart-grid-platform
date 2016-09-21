package com.alliander.osgp.dto.valueobjects.microgrids;

import java.io.Serializable;
import java.util.List;

public class DataRequestDto implements Serializable {

    private static final long serialVersionUID = -2708314693698798777L;

    private List<SystemFilterDto> systemFilters;

    public DataRequestDto(final List<SystemFilterDto> systemFilters) {
        super();
        this.systemFilters = systemFilters;
    }

    public List<SystemFilterDto> getSystemFilters() {
        return this.systemFilters;
    }

    public void setSystemFilters(final List<SystemFilterDto> systemFilters) {
        this.systemFilters = systemFilters;
    }
}
