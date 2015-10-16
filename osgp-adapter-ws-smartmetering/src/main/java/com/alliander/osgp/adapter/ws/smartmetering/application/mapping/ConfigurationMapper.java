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

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDaysRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotification;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmType;

@Component(value = "configurationMapper")
public class ConfigurationMapper extends ConfigurableMapper {

    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory
                .classMap(SpecialDaysRequest.class,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest.class).byDefault()
                .register();
        mapperFactory
        .classMap(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotifications.class,
                AlarmNotifications.class).byDefault().register();
        mapperFactory
        .classMap(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotification.class,
                AlarmNotification.class).byDefault().register();
        mapperFactory
        .classMap(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmType.class,
                AlarmType.class).byDefault().register();
    }
}
