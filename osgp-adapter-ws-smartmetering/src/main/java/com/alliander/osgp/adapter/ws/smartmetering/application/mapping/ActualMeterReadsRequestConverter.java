/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsRequest;

public class ActualMeterReadsRequestConverter
extends
BidirectionalConverter<ActualMeterReadsRequest, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest convertTo(
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsRequest source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest> destinationType) {

        final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest destination = new com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest();
        destination.setDeviceIdentification(source.getDeviceIdentification());

        return destination;
    }

    @Override
    public ActualMeterReadsRequest convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsRequest source,
            final Type<com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsRequest> destinationType) {

        return new ActualMeterReadsRequest(source.getDeviceIdentification());
    }

}
