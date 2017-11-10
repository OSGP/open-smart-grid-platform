/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.builtin.DateAndTimeConverters.DateToXmlGregorianCalendarConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters.DlmsMeterValueConverter;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters.ProfileEntryValueConverter;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters.ReadAlarmRegisterDataConverter;

@Component(value = "monitoringMapper")
public class MonitoringMapper extends ConfigurableMapper {

    @Override
    public final void configure(final MapperFactory mapperFactory) {

        // This converter must be used: a multiplier might be needed when
        // mapping between DlmsMeterValue and OsgpMeterValue. Thus mapping must
        // never be attempted without using this converter!
        mapperFactory.getConverterFactory().registerConverter(new DlmsMeterValueConverter());
        mapperFactory.getConverterFactory().registerConverter(new ReadAlarmRegisterDataConverter());
        mapperFactory.getConverterFactory().registerConverter(new ProfileEntryValueConverter());
        mapperFactory.getConverterFactory().registerConverter(new DateToXmlGregorianCalendarConverter());
    }

}
