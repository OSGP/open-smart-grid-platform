/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.stereotype.Component;

import org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.ws.HistoryTermTypeConverter;
import org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.ws.PowerUsageMeterTypeConverter;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToDateTimeConverter;

@Component(value = "publicLightingDeviceMonitoringMapper")
public class DeviceMonitoringMapper extends ConfigurableMapper {

    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(new PowerUsageMeterTypeConverter());
        mapperFactory.getConverterFactory().registerConverter(new HistoryTermTypeConverter());
        mapperFactory.getConverterFactory().registerConverter(new XMLGregorianCalendarToDateTimeConverter());
    }
}
