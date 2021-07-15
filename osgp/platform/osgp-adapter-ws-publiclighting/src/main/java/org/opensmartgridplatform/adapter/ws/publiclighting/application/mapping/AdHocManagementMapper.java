/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping;

import org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.converters.LmdConverter;
import org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.converters.SsldConverter;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToDateTimeConverter;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

@Component(value = "publicLightingAdhocManagementMapper")
public class AdHocManagementMapper extends ConfigurableMapper {

    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(new XMLGregorianCalendarToDateTimeConverter());
        mapperFactory.getConverterFactory().registerConverter(new SsldConverter());
        mapperFactory.getConverterFactory().registerConverter(new LmdConverter());
    }
}
