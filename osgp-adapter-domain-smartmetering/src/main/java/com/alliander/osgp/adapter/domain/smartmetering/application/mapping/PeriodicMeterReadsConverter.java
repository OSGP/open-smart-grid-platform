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

public class PeriodicMeterReadsConverter extends
BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequest, PeriodicMeterReadsRequest> {

    @Override
    public PeriodicMeterReadsRequest convertTo(com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequest source, Type<PeriodicMeterReadsRequest> destinationType) {
        PeriodicMeterReadsRequest periodicMeterReadsRequest = new PeriodicMeterReadsRequest(source.getDeviceIdentification());
        for (PeriodicMeterReadsRequestData pmrd : source.getPeriodicMeterReadsRequestData()) {
            periodicMeterReadsRequest.addPeriodicMeterReadsRequestData(new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequestData(
                    pmrd.getDeviceIdentification(), PeriodType.valueOf(pmrd.getPeriodType().name()), pmrd.getBeginDate(), pmrd.getEndDate())
            );
        }
        return periodicMeterReadsRequest;
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequest convertFrom(PeriodicMeterReadsRequest source, Type<com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequest> destinationType) {
        com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequest periodicMeterReadsRequest = 
                new com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequest(source.getDeviceIdentification());
        for (com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequestData pmrd : source.getPeriodicMeterReadsRequestData()) {
            periodicMeterReadsRequest.addPeriodicMeterReadsRequestData(new PeriodicMeterReadsRequestData(
                    pmrd.getDeviceIdentification(), com.alliander.osgp.dto.valueobjects.smartmetering.PeriodType.valueOf(pmrd.getPeriodType().name()), pmrd.getBeginDate(), pmrd.getEndDate())
            );
        }
        return periodicMeterReadsRequest;
    }

}
