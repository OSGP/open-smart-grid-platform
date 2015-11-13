/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequest;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestData;
import java.util.ArrayList;
import java.util.List;

public class PeriodicMeterReadsConverter extends
BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequest, PeriodicMeterReadsRequest> {

    @Override
    public PeriodicMeterReadsRequest convertTo(com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequest source, Type<PeriodicMeterReadsRequest> destinationType) {
        final List<com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequestData> periodicMeterReadsRequestData = new ArrayList<>();
        for (PeriodicMeterReadsRequestData pmrd : source.getPeriodicMeterReadsRequestData()) {
            periodicMeterReadsRequestData.add(new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequestData(
                    pmrd.getDeviceIdentification(), PeriodType.valueOf(pmrd.getPeriodType().name()), pmrd.getBeginDate(), pmrd.getEndDate())
            );
        }
        return new PeriodicMeterReadsRequest(source.getDeviceIdentification(),periodicMeterReadsRequestData);
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequest convertFrom(PeriodicMeterReadsRequest source, Type<com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequest> destinationType) {
        List<PeriodicMeterReadsRequestData> periodicMeterReadsRequestData = new ArrayList<>();
        for (com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequestData pmrd : source.getPeriodicMeterReadsRequestData()) {
            periodicMeterReadsRequestData.add(new PeriodicMeterReadsRequestData(
                    pmrd.getDeviceIdentification(), com.alliander.osgp.dto.valueobjects.smartmetering.PeriodType.valueOf(pmrd.getPeriodType().name()), pmrd.getBeginDate(), pmrd.getEndDate())
            );
        }
        return new com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequest(source.getDeviceIdentification(),periodicMeterReadsRequestData);
    }

}
