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

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCode;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;

@Component(value = "monitoringMapper")
public class MonitoringMapper extends ConfigurableMapper {
    @Override
    public void configure(final MapperFactory mapperFactory) {

        // These classMaps are needed because of different names for fields.
        mapperFactory.classMap(PeriodicMeterReadsContainerGas.class, PeriodicMeterReadsGasResponse.class)
        .field("meterReadsGas", "periodicMeterReadsGas").byDefault().register();
        mapperFactory
        .classMap(com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusCode.class,
                AmrProfileStatusCode.class).field("amrProfileStatusCodeFlag", "amrProfileStatusCodeFlags")
                .byDefault().register();

        // Converter is needed because of instanceOf check to set boolean
        // mbusDevice for a PeriodicMeterReadsQuery object
        mapperFactory.getConverterFactory().registerConverter(new PeriodicMeterReadsRequestConverter());

        // This converter is needed because
        // RetrievePushNotificationAlarmResponse
        // doesn't have a List<AlarmType>, but an AlarmRegister. The
        // List<AlarmType> is in that class.
        mapperFactory.getConverterFactory().registerConverter(new PushNotificationsAlarmConverter());

        // This converter is needed because of the M_3 in the OsgpUnitType enum
        // and the M3 in the OsgpUnit enum.
        mapperFactory.getConverterFactory().registerConverter(new MeterValueConverter());

        // This converter is needed to ensure correct mapping of dates and
        // times.
        mapperFactory.getConverterFactory().registerConverter(new XsdDateTimeToLongConverter());

        // This converter is needed because it contains logic.
        mapperFactory.getConverterFactory().registerConverter(new PeriodicReadsRequestGasQueryConverter());
    }

}
