/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotifications;

@Component(value = "configurationMapper")
public class ConfigurationMapper extends ConfigurableMapper {

    @Override
    public void configure(final MapperFactory mapperFactory) {

        // This converter is necessary because of the SeasonsType object in
        // ActivityCalendarType.
        mapperFactory.getConverterFactory().registerConverter(new ActivityCalendarConverter());

        // This classMap replaces the AlarmNotificationsConverter, is needed
        // because of different field names.
        mapperFactory
                .classMap(AlarmNotifications.class,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications.class)
                .field("alarmNotification", "alarmNotificationsSet").byDefault().register();
        mapperFactory.getConverterFactory().registerConverter(new AlarmNotificationsConverter());

        // These two converters are needed because they combine two fields
        // into one SendDestinationAndMethod object (or split the object into
        // two fields)
        mapperFactory.getConverterFactory().registerConverter(new PushSetupAlarmConverter());
        mapperFactory.getConverterFactory().registerConverter(new PushSetupSmsConverter());

        // These converters are necessary to enable correct mapping of dates and
        // times.
        mapperFactory.getConverterFactory().registerConverter(new CosemDateTimeConverter());
        mapperFactory.getConverterFactory().registerConverter(new CosemTimeConverter());
        mapperFactory.getConverterFactory().registerConverter(new CosemDateConverter());
        mapperFactory.getConverterFactory().registerConverter(new XsdDateTimeToLongConverter());
    }
}
