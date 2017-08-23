package com.alliander.osgp.cucumber.platform.smartmetering.config;

import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.mappers.XMLGregorianCalendarToDateTimeConverter;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

@Component
public class SmartMeteringMapper extends ConfigurableMapper {

    @Override
    protected void configure(final MapperFactory factory) {
        factory.getConverterFactory().registerConverter(new XMLGregorianCalendarToDateTimeConverter());
    }
}
