/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.admin.application.mapping.ws;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.EventType;

public class EventTypeConverter extends
        CustomConverter<EventType, com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.EventType> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.EventType convert(final EventType source,
            final Type<? extends com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.EventType> destinationType) {
        return com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.EventType.fromValue(source.name());
    }
}
