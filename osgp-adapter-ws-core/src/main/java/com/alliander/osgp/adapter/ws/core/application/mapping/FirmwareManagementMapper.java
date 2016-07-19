/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping;

import javax.annotation.PostConstruct;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.FirmwareVersion;
import com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable.WritableFirmwareRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.dto.valueobjects.FirmwareVersionDto;
import com.alliander.osgp.shared.mappers.XMLGregorianCalendarToDateTimeConverter;

@Component(value = "coreFirmwareManagementMapper")
public class FirmwareManagementMapper extends ConfigurableMapper {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private WritableFirmwareRepository firmwareRepository;

    public FirmwareManagementMapper() {
        // Setting auto init to false, to make sure the repo
        super(false);
    }

    @PostConstruct
    public void initialize() {
        this.init();
    }

    @Override
    public void configure(final MapperFactory mapperFactory) {

        mapperFactory.getConverterFactory().registerConverter(new FirmwareConverter());

        mapperFactory.getConverterFactory().registerConverter(
                new DeviceFirmwareConverter(this.deviceRepository, this.firmwareRepository));
        mapperFactory.classMap(FirmwareVersion.class, FirmwareVersionDto.class).byDefault().register();

        mapperFactory.registerClassMap(mapperFactory
                .classMap(com.alliander.osgp.domain.core.entities.DeviceModel.class,
                        com.alliander.osgp.adapter.ws.schema.core.firmwaremanagement.DeviceModel.class)
                        .field("manufacturerId.manufacturerId", "manufacturer").byDefault().toClassMap());

        mapperFactory.getConverterFactory().registerConverter(new XMLGregorianCalendarToDateTimeConverter());
    }

}
