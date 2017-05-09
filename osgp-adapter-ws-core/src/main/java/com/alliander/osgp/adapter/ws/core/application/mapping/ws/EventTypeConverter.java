/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping.ws;

import com.alliander.osgp.domain.core.valueobjects.EventType;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class EventTypeConverter
        extends CustomConverter<EventType, com.alliander.osgp.adapter.ws.schema.core.devicemanagement.EventType> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.core.devicemanagement.EventType convert(final EventType source,
            final Type<? extends com.alliander.osgp.adapter.ws.schema.core.devicemanagement.EventType> destinationType,
            final MappingContext context) {
        return com.alliander.osgp.adapter.ws.schema.core.devicemanagement.EventType.fromValue(source.name());
    }
}
