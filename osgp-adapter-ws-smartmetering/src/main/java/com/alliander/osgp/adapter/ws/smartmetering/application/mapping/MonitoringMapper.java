/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpUnitType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.MeterValue;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpMeterValue;

@Component(value = "monitoringMapper")
public class MonitoringMapper extends ConfigurableMapper {
    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(new ActualMeterReadsConverter());
        mapperFactory.getConverterFactory().registerConverter(new ActualMeterReadsGasConverter());
        mapperFactory.getConverterFactory().registerConverter(new AlarmRegisterConverter());
        mapperFactory.getConverterFactory().registerConverter(new PeriodicMeterReadsRequestConverter());
        mapperFactory.getConverterFactory().registerConverter(new PeriodicMeterReadsResponseConverter());
        mapperFactory.getConverterFactory().registerConverter(new PeriodicMeterReadsResponseGasConverter());
        mapperFactory.getConverterFactory().registerConverter(new AmrProfileStatusCodeConverter());
        mapperFactory.getConverterFactory().registerConverter(new PushNotificationsAlarmConverter());
    }

    /**
     * helper method to construct a MeterValue, code in one place
     *
     * @param d
     * @param osgpUnit
     * @return
     */
    public static MeterValue getMeterValue(final OsgpMeterValue osgpMeterValue) {
        if (osgpMeterValue == null) {
            return null;
        }
        final MeterValue m = new MeterValue();
        m.setValue(osgpMeterValue.getValue());
        m.setUnit(OsgpUnitType.valueOf(osgpMeterValue.getOsgpUnit().name()));
        return m;
    }
}
