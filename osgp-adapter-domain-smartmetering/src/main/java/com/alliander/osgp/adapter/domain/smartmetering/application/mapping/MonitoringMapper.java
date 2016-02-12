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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "monitoringMapper")
public class MonitoringMapper extends ConfigurableMapper {

    public static final String SCALERUNIT = "scalerunit";

    @Autowired
    private StandardUnitCalculator standardUnitCalculator;

    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(new AlarmRegisterConverter());
        mapperFactory.getConverterFactory().registerConverter(
                new ActualMeterReadsConverter(this.standardUnitCalculator));
        mapperFactory.getConverterFactory().registerConverter(
                new ActualMeterReadsGasConverter(this.standardUnitCalculator));
        mapperFactory.getConverterFactory().registerConverter(new PeriodicMeterReadsRequestConverter());
        mapperFactory.getConverterFactory().registerConverter(
                new PeriodicMeterReadsResponseConverter(this.standardUnitCalculator));
        mapperFactory.getConverterFactory().registerConverter(
                new PeriodicMeterReadsGasResponseConverter(this.standardUnitCalculator));

        mapperFactory.getConverterFactory().registerConverter(new AmrProfileStatusCodeConverter());
    }

}
