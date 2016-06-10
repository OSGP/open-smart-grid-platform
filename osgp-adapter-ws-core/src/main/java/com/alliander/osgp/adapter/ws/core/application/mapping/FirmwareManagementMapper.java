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

import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FirmwareVersion;
import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;

@Component(value = "coreFirmwareManagementMapper")
public class FirmwareManagementMapper extends ConfigurableMapper {

    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.classMap(FirmwareVersion.class, FirmwareVersionDto.class).byDefault().register();

        mapperFactory.registerClassMap(mapperFactory
                .classMap(com.alliander.osgp.domain.core.entities.DeviceModel.class,
                        com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.DeviceModel.class)
                        .field("manufacturerId.manufacturerId", "manufacturer").byDefault().toClassMap());
    }

}
