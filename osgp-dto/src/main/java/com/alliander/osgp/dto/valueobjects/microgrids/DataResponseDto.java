package com.alliander.osgp.dto.valueobjects.microgrids;

import java.io.Serializable;
import java.util.List;

public class DataResponseDto implements Serializable {

    private static final long serialVersionUID = 5903337694184574498L;

    private List<MeasurementResultSystemIdentifierDto> measurementResultSystemIdentifiers;

    public DataResponseDto(final List<MeasurementResultSystemIdentifierDto> measurementResultSystemIdentifiers) {
        this.measurementResultSystemIdentifiers = measurementResultSystemIdentifiers;
    }

    public List<MeasurementResultSystemIdentifierDto> getMeasurementResultSystemIdentifiers() {
        return this.measurementResultSystemIdentifiers;
    }

    public void setMeasurementResultSystemIdentifiers(
            final List<MeasurementResultSystemIdentifierDto> measurementResultSystemIdentifiers) {
        this.measurementResultSystemIdentifiers = measurementResultSystemIdentifiers;
    }
}
