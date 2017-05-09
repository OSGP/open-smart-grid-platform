/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class ReadAlarmRegisterRequestConverter extends
        BidirectionalConverter<ReadAlarmRegisterRequest, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest convertTo(
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest> destinationType,
            final MappingContext context) {

        final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest destination = new com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest();
        destination.setDeviceIdentification(source.getDeviceIdentification());

        return destination;
    }

    @Override
    public ReadAlarmRegisterRequest convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest source,
            final Type<com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest> destinationType,
            final MappingContext context) {

        return new ReadAlarmRegisterRequest(source.getDeviceIdentification());
    }

}
