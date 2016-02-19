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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component(value = "monitoringMapper")
public class MonitoringMapper extends ConfigurableMapper {

    private ApplicationContext applicationContext;

    public MonitoringMapper() {
        // init after autowire
        super(false);
    }

    @Autowired
    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.init();
    }

    @Override
    public final void configure(final MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(
                this.applicationContext.getBean(AlarmRegisterConverter.class));
        mapperFactory.getConverterFactory().registerConverter(
                this.applicationContext.getBean(ActualMeterReadsConverter.class));
        mapperFactory.getConverterFactory().registerConverter(
                this.applicationContext.getBean(ActualMeterReadsGasConverter.class));
        mapperFactory.getConverterFactory().registerConverter(
                this.applicationContext.getBean(PeriodicMeterReadsRequestConverter.class));
        mapperFactory.getConverterFactory().registerConverter(
                this.applicationContext.getBean(PeriodicMeterReadsResponseConverter.class));
        mapperFactory.getConverterFactory().registerConverter(
                this.applicationContext.getBean(PeriodicMeterReadsGasResponseConverter.class));

        mapperFactory.getConverterFactory().registerConverter(
                this.applicationContext.getBean(AmrProfileStatusCodeConverter.class));
    }

}
