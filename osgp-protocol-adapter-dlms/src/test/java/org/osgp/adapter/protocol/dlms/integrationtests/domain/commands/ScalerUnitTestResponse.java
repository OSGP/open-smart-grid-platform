/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.integrationtests.domain.commands;

import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnit;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScalerUnitResponse;

public class ScalerUnitTestResponse implements ScalerUnitResponse {

    private final ScalerUnit scalerUnit;

    public ScalerUnitTestResponse(final ScalerUnit scalerUnit) {
        this.scalerUnit = scalerUnit;
    }

    @Override
    public ScalerUnit getScalerUnit() {
        return this.scalerUnit;
    }

}
