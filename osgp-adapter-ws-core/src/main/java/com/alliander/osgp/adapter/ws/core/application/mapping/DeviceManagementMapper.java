/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.core.application.mapping.ws.EventTypeConverter;
import com.alliander.osgp.shared.mappers.XMLGregorianCalendarToDateTimeConverter;

@Component(value = "coreDeviceManagementMapper")
public class DeviceManagementMapper extends ConfigurableMapper {

    @Override
    public void configure(final MapperFactory mapperFactory) {

        mapperFactory.registerClassMap(mapperFactory
                .classMap(com.alliander.osgp.domain.core.entities.Event.class,
                        com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Event.class)
                .field("device.deviceIdentification", "deviceIdentification").field("creationTime", "timestamp")
                .field("event", "eventType").byDefault().toClassMap());

        mapperFactory.getConverterFactory().registerConverter(new XMLGregorianCalendarToDateTimeConverter());
        mapperFactory.getConverterFactory().registerConverter(new EventTypeConverter());
    }
}
