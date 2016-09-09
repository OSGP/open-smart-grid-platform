/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters.FirmwareVersionConverter;

@Component(value = "configurationMapper")
public class ConfigurationMapper extends ConfigurableMapper {

    @Override
    public void configure(final MapperFactory mapperFactory) {

        // This mapper needs a converter for CosemDateTime objects because
        // Orika sometimes throws an exception if mapping by default is tried
        mapperFactory.getConverterFactory().registerConverter(new CosemDateTimeConverter(this));
        mapperFactory.getConverterFactory().registerConverter(new AdministrativeStatusResponseConverter());
        mapperFactory.getConverterFactory().registerConverter(new FirmwareVersionConverter());
    }
}
