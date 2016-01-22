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
