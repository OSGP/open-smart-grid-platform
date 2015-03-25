package com.alliander.osgp.adapter.domain.publiclighting.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.stereotype.Component;

@Component
public class DomainPublicLightingMapper extends ConfigurableMapper {

    @Override
    protected void configure(final MapperFactory factory) {
        factory.getConverterFactory().registerConverter(new LightTypeConverter());
        factory.getConverterFactory().registerConverter(new LinkTypeConverter());
        factory.getConverterFactory().registerConverter(new PowerUsageDataConverter());
        factory.getConverterFactory().registerConverter(new ScheduleConverter());
    }
}
