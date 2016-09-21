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

public class SystemFilter extends SystemIdentifier implements Serializable {

    private static final long serialVersionUID = 2069822566541617223L;

    private final List<MeasurementFilter> measurementFilters;
    private final boolean all;

    public SystemFilter(final int id, final String systemType, final List<MeasurementFilter> measurementFilters,
            final boolean all) {
        super(id, systemType);
        this.measurementFilters = new ArrayList<MeasurementFilter>(measurementFilters);
        this.all = all;
    }

    public List<MeasurementFilter> getMeasurementFilters() {
        return this.measurementFilters;
    }

    public boolean isAll() {
        return this.all;
    }
}
