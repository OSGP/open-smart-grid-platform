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

    @Autowired
    private StandardUnitCalculator standardUnitCalculator;

    private MapperFactory mapperFactory;

    @Override
    public final void configure(final MapperFactory mapperFactory) {
        /*
         * configure is called at construction time, before autowiring causing
         * npe's therefore postpone configure to until autowiring is done
         */
        this.mapperFactory = mapperFactory;
    }

    /**
     * will be called from spring autowiring, converters will be registered here
     *
     * @param standardUnitCalculator
     */
    public final void setStandardUnitCalculator(final StandardUnitCalculator standardUnitCalculator) {
        this.standardUnitCalculator = standardUnitCalculator;
        this.mapperFactory.getConverterFactory().registerConverter(new AlarmRegisterConverter());
        this.mapperFactory.getConverterFactory().registerConverter(
                new ActualMeterReadsConverter(this.standardUnitCalculator));
        this.mapperFactory.getConverterFactory().registerConverter(
                new ActualMeterReadsGasConverter(this.standardUnitCalculator));
        this.mapperFactory.getConverterFactory().registerConverter(new PeriodicMeterReadsRequestConverter());
        this.mapperFactory.getConverterFactory().registerConverter(
                new PeriodicMeterReadsResponseConverter(this.standardUnitCalculator));
        this.mapperFactory.getConverterFactory().registerConverter(
                new PeriodicMeterReadsGasResponseConverter(this.standardUnitCalculator));

        this.mapperFactory.getConverterFactory().registerConverter(new AmrProfileStatusCodeConverter());
    }

}
