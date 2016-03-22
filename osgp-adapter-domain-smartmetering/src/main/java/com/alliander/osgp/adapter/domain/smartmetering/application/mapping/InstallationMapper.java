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

import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import com.alliander.osgp.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;

@Component(value = "installationMapper")
public class InstallationMapper extends ConfigurableMapper {
    @Override
    public void configure(final MapperFactory mapperFactory) {

        // domain value object -> SmartMeteringDevice entity class
        mapperFactory.classMap(SmartMeter.class, SmartMeteringDevice.class).byDefault().register();

        // domain value object -> dto value object
        mapperFactory.classMap(SmartMeteringDevice.class, SmartMeteringDeviceDto.class).byDefault().register();
    }
}
