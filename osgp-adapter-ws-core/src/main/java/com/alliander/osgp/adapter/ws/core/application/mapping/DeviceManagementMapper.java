/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping;

import java.util.Objects;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.core.application.mapping.ws.EventTypeConverter;
import com.alliander.osgp.adapter.ws.core.application.mapping.ws.ScheduledTaskConverter;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.shared.mappers.XMLGregorianCalendarToDateTimeConverter;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

@Component(value = "coreDeviceManagementMapper")
public class DeviceManagementMapper extends ConfigurableMapper {

    @Autowired
    private SsldRepository ssldRepository;

    public DeviceManagementMapper() {
        super(false);
    }

    protected DeviceManagementMapper(final SsldRepository ssldRepository) {
        super(false);
        this.ssldRepository = ssldRepository;
    }

    @PostConstruct
    public void initialize() {
        this.init();
    }

    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory
                .classMap(com.alliander.osgp.domain.core.entities.Device.class,
                        com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device.class)
                .field("ipAddress", "networkAddress").byDefault().toClassMap());

        mapperFactory.registerClassMap(mapperFactory
                .classMap(com.alliander.osgp.domain.core.entities.Event.class,
                        com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Event.class)
                .field("device.deviceIdentification", "deviceIdentification").field("dateTime", "timestamp").byDefault()
                .toClassMap());

        mapperFactory.getConverterFactory().registerConverter(new XMLGregorianCalendarToDateTimeConverter());
        mapperFactory.getConverterFactory().registerConverter(new EventTypeConverter());
        mapperFactory.getConverterFactory().registerConverter(new SmartMeterConverter());
        mapperFactory.getConverterFactory().registerConverter(new DeviceConverter());
        mapperFactory.getConverterFactory().registerConverter(new LightMeasurementDeviceConverter());
        mapperFactory.getConverterFactory().registerConverter(new SsldConverter(this.ssldRepository));
        mapperFactory.getConverterFactory().registerConverter(new ScheduledTaskConverter());
    }

    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hashCode(this.ssldRepository);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        return super.equals(obj) && Objects.equals(this.ssldRepository, ((DeviceManagementMapper) obj).ssldRepository);
    }

}
