/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping;

import ma.glasnost.orika.metadata.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.domain.core.entities.SmartMeter;

class SmartMeterConverter extends AbstractDeviceConverter<SmartMeter> {
    static final Logger LOGGER = LoggerFactory.getLogger(SmartMeterConverter.class);

    @Override
    public SmartMeter convertFrom(final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device source,
            final Type<SmartMeter> destinationType) {
        return this.initEntity(source, SmartMeter.class);
    }

    @Override
    public com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device convertTo(final SmartMeter source,
            final Type<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device> destinationType) {
        return this.initJaxb(source);
    }

}