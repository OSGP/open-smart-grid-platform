/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.microgrids.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataResponse implements Serializable {

    private static final long serialVersionUID = 7345936024521675762L;

    private final List<MeasurementResultSystemIdentifier> measurementResultSystemIdentifiers;

    public DataResponse(final List<MeasurementResultSystemIdentifier> measurementResultSystemIdentifiers) {
        this.measurementResultSystemIdentifiers = new ArrayList<MeasurementResultSystemIdentifier>(
                measurementResultSystemIdentifiers);
    }

    public List<MeasurementResultSystemIdentifier> getMeasurementResultSystemIdentifiers() {
        return new ArrayList<>(this.measurementResultSystemIdentifiers);
    }

}
