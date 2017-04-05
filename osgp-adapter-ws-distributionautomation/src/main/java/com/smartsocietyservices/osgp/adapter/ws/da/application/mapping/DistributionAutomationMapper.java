/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.smartsocietyservices.osgp.adapter.ws.da.application.mapping;

import com.alliander.osgp.shared.mappers.XMLGregorianCalendarToDateTimeConverter;
import com.smartsocietyservices.osgp.domain.da.valueobjects.GetDeviceModelRequest;
import com.smartsocietyservices.osgp.domain.da.valueobjects.GetHealthStatusRequest;
import com.smartsocietyservices.osgp.domain.da.valueobjects.GetPQValuesPeriodicRequest;
import com.smartsocietyservices.osgp.domain.da.valueobjects.GetPQValuesRequest;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class DistributionAutomationMapper extends ConfigurableMapper {

    private static final String SYSTEM = "system";
    private static final String SYSTEM_TYPE = "systemType";
    private static final String TYPE = "type";

    @Override
    public void configure( final MapperFactory mapperFactory ) {
        mapperFactory.getConverterFactory().registerConverter( new PassThroughConverter( DateTime.class ) );
        mapperFactory.getConverterFactory().registerConverter( new XMLGregorianCalendarToDateTimeConverter() );

        mapperFactory.classMap( com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.generic.GetPQValuesRequest.class,
                GetPQValuesRequest.class ).byDefault().register();

        mapperFactory.classMap( com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.generic.GetPQValuesPeriodicRequest.class,
                GetPQValuesPeriodicRequest.class ).byDefault().register();

        mapperFactory.classMap( com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.generic.GetDeviceModelRequest.class,
                GetDeviceModelRequest.class ).byDefault().register();

        mapperFactory.classMap( com.smartsocietyservices.osgp.adapter.ws.schema.distributionautomation.generic.GetHealthStatusRequest.class,
                GetHealthStatusRequest.class ).byDefault().register();
    }
}
