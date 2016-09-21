package com.alliander.osgp.dto.valueobjects.microgrids;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class MeasurementResultSystemIdentifierDto extends SystemIdentifierDto implements Serializable {

    private static final long serialVersionUID = -1981029545705567105L;

    private List<MeasurementDto> measurements;

    public MeasurementResultSystemIdentifierDto(final int id, final String systemType,
            final List<MeasurementDto> measurements) {
        super(id, systemType);
        this.measurements = measurements;
    }

    public List<MeasurementDto> getMeasurements() {
        return Collections.unmodifiableList(this.measurements);
    }

    public void setMeasurements(final List<MeasurementDto> measurements) {
        this.measurements = measurements;
    }
}
