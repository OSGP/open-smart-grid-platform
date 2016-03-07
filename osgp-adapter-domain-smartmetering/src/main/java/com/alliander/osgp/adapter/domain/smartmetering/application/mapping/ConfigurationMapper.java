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

@Component(value = "configurationMapper")
public class ConfigurationMapper extends ConfigurableMapper {

    @Override
    public void configure(final MapperFactory mapperFactory) {

        // domain value object -> dto value object
        mapperFactory
        .classMap(com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest.class,
                com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequest.class).byDefault()
                .register();

        mapperFactory.getConverterFactory().registerConverter(new AlarmNotificationsConverter());
        mapperFactory.getConverterFactory().registerConverter(new ConfigurationObjectConverter());
        mapperFactory.getConverterFactory().registerConverter(new CosemObisCodeConverter());
        mapperFactory.getConverterFactory().registerConverter(new SendDestinationAndMethodConverter());
        mapperFactory.getConverterFactory().registerConverter(new CosemDateConverter());
        mapperFactory.getConverterFactory().registerConverter(new CosemTimeConverter());
        mapperFactory.getConverterFactory().registerConverter(new CosemDateTimeConverter(this));
        mapperFactory.getConverterFactory().registerConverter(new WindowElementConverter(this));
        mapperFactory.getConverterFactory().registerConverter(new CosemObjectDefinitionConverter(this));
        mapperFactory.getConverterFactory().registerConverter(new PushSetupAlarmConverter());
        mapperFactory.getConverterFactory().registerConverter(new PushSetupSmsConverter());
        mapperFactory.getConverterFactory().registerConverter(new ActivityCalendarConverter());
        mapperFactory.getConverterFactory().registerConverter(new AdministrativeStatusConverter());
        mapperFactory.getConverterFactory().registerConverter(new KeySetConverter());
        mapperFactory.getConverterFactory().registerConverter(new SpecialDaysDataConverter());
        mapperFactory.getConverterFactory().registerConverter(new SpecialDayConverter());
    }
}
