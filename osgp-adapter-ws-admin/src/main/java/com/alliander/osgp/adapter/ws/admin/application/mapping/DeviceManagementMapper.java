/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.admin.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.admin.application.mapping.ws.EventTypeConverter;
import com.alliander.osgp.shared.mappers.XMLGregorianCalendarToDateTimeConverter;

@Component(value = "adminDeviceManagementMapper")
public class DeviceManagementMapper extends ConfigurableMapper {

    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory
                .classMap(com.alliander.osgp.logging.domain.entities.DeviceLogItem.class,
                        com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.MessageLog.class)
                        .field("modificationTime", "timestamp").byDefault().toClassMap());

        mapperFactory.registerClassMap(mapperFactory
                .classMap(com.alliander.osgp.domain.core.entities.DeviceAuthorization.class,
                        com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.DeviceAuthorisation.class)
                        .field("device.deviceIdentification", "deviceIdentification")
                        .field("organisation.organisationIdentification", "organisationIdentification").byDefault()
                        .toClassMap());

        mapperFactory.registerClassMap(mapperFactory
                .classMap(com.alliander.osgp.domain.core.entities.Event.class,
                        com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.Event.class)
                        .field("device.deviceIdentification", "deviceIdentification").field("creationTime", "timestamp")
                        .byDefault().toClassMap());

        mapperFactory.registerClassMap(mapperFactory
                .classMap(com.alliander.osgp.domain.core.entities.ProtocolInfo.class,
                        com.alliander.osgp.adapter.ws.schema.admin.devicemanagement.ProtocolInfo.class)
                .exclude("outgoingProtocolRequestsQueue").exclude("incomingProtocolResponsesQueue")
                .exclude("incomingProtocolRequestsQueue").exclude("outgoingProtocolResponsesQueue").byDefault()
                .toClassMap());

        mapperFactory.getConverterFactory().registerConverter(new XMLGregorianCalendarToDateTimeConverter());
        mapperFactory.getConverterFactory().registerConverter(new EventTypeConverter());
    }
}
