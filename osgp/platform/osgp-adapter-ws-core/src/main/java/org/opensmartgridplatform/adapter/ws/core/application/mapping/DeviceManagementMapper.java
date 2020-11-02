/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import java.util.Objects;

import javax.annotation.PostConstruct;

import org.opensmartgridplatform.adapter.ws.core.application.mapping.ws.EventTypeConverter;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.shared.mappers.XMLGregorianCalendarToDateTimeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.Mapper;
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
                .classMap(org.opensmartgridplatform.domain.core.entities.Device.class,
                        org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device.class)
                .field("ipAddress", "networkAddress")
                .byDefault()
                .toClassMap());

        mapperFactory.registerClassMap(mapperFactory
                .classMap(org.opensmartgridplatform.domain.core.entities.Event.class,
                        org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Event.class)
                .field("dateTime", "timestamp")
                .byDefault()
                .toClassMap());

        mapperFactory.registerClassMap(mapperFactory
                .classMap(org.opensmartgridplatform.domain.core.entities.ScheduledTaskWithoutData.class,
                        org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.ScheduledTask.class)
                .byDefault()
                .toClassMap());

        mapperFactory.registerClassMap(mapperFactory
                .classMap(org.opensmartgridplatform.domain.core.entities.ScheduledTaskWithoutData.class,
                        org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.ScheduledTask.class)
                .byDefault()
                .toClassMap());

        final Mapper<org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdatedDevice, Ssld> deviceOutputSettingsMapper = new DeviceOutputSettingsMapper();

        mapperFactory
                .classMap(org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.UpdatedDevice.class,
                        Ssld.class)
                .exclude("outputSettings")
                .field("gpsLatitude", "gpsCoordinates.latitude")
                .field("gpsLongitude", "gpsCoordinates.longitude")
                .byDefault()
                .customize(deviceOutputSettingsMapper)
                .register();

        mapperFactory.getConverterFactory().registerConverter(new XMLGregorianCalendarToDateTimeConverter());
        mapperFactory.getConverterFactory().registerConverter(new EventTypeConverter());
        mapperFactory.getConverterFactory().registerConverter(new SmartMeterConverter());
        mapperFactory.getConverterFactory().registerConverter(new DeviceConverter());
        mapperFactory.getConverterFactory().registerConverter(new LightMeasurementDeviceConverter());
        mapperFactory.getConverterFactory().registerConverter(new SsldConverter(this.ssldRepository));
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
