package com.alliander.osgp.adapter.ws.tariffswitching.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.mappers.XMLGregorianCalendarToDateTimeConverter;

@Component(value = "tariffSwitchingAdhocManagementMapper")
public class AdHocManagementMapper extends ConfigurableMapper {
    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(new XMLGregorianCalendarToDateTimeConverter());
    }
}
