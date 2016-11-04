/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.microgrids;

import java.io.Serializable;
import java.util.List;

public class GetDataRequestDto implements Serializable {

    private static final long serialVersionUID = -2708314693698798777L;

    private List<SystemFilterDto> systemFilters;

    public GetDataRequestDto(final List<SystemFilterDto> systemFilters) {
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
