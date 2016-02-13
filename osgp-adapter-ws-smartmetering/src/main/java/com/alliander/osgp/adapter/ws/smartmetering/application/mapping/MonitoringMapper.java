/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.math.BigDecimal;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.EMeterValue;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.GMeterValue;

@Component(value = "monitoringMapper")
public class MonitoringMapper extends ConfigurableMapper {
    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(new ActualMeterReadsConverter());
        mapperFactory.getConverterFactory().registerConverter(new ActualMeterReadsGasConverter());
        mapperFactory.getConverterFactory().registerConverter(new AlarmRegisterConverter());
        mapperFactory.getConverterFactory().registerConverter(new PeriodicMeterReadsRequestConverter());
        mapperFactory.getConverterFactory().registerConverter(new PeriodicMeterReadsConverter());
        mapperFactory.getConverterFactory().registerConverter(new PeriodicMeterReadsGasConverter());
        mapperFactory.getConverterFactory().registerConverter(new AmrProfileStatusCodeConverter());
        mapperFactory.getConverterFactory().registerConverter(new PushNotificationsAlarmConverter());
    }

    public static EMeterValue eFromDouble(final double value) {
        final EMeterValue eMeterValue = new EMeterValue();
        eMeterValue.setValue(new BigDecimal(value));
        return eMeterValue;
    }

    public static GMeterValue gFromDouble(final double value) {
        final GMeterValue eMeterValue = new GMeterValue();
        eMeterValue.setValue(new BigDecimal(value));
        return eMeterValue;
    }

}
