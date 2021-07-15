/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import javax.annotation.PostConstruct;

import org.opensmartgridplatform.adapter.ws.shared.db.domain.repositories.writable.WritableDeviceModelRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

@Component(value = "coreDeviceInstallationMapper")
public class DeviceInstallationMapper extends ConfigurableMapper {

    @Autowired
    private SsldRepository ssldRepository;

    @Autowired
    private WritableDeviceModelRepository writableDeviceModelRepository;

    public DeviceInstallationMapper() {
        super(false);
    }

    @PostConstruct
    public void initialize() {
        this.init();
    }

    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory()
                .registerConverter(new WsInstallationDeviceToDeviceConverter(this.ssldRepository,
                        this.writableDeviceModelRepository));
        mapperFactory.getConverterFactory().registerConverter(new WsInstallationDeviceToSsldConverter());
        mapperFactory.getConverterFactory()
                .registerConverter(new WsInstallationLmdToLmdConverter(this.writableDeviceModelRepository));
    }
}
