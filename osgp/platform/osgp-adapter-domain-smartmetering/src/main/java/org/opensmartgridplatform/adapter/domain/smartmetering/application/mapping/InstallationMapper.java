/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters.DeCoupleMbusDeviceByChannelResponseConverter;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "installationMapper")
public class InstallationMapper extends ConfigurableMapper {

    @Autowired
    private SmartMeterRepository smartMeteringDeviceRepository;

    @Override
    public final void configure(final MapperFactory mapperFactory) {
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter(new DeCoupleMbusDeviceByChannelResponseConverter(smartMeteringDeviceRepository));
    }

}
