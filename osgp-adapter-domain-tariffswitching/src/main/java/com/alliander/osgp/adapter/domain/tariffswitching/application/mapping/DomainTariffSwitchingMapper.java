package com.alliander.osgp.adapter.domain.tariffswitching.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.stereotype.Component;

@Component
public class DomainTariffSwitchingMapper extends ConfigurableMapper {

    @Override
    protected void configure(final MapperFactory factory) {
        factory.getConverterFactory().registerConverter(new LightTypeConverter());
        factory.getConverterFactory().registerConverter(new LinkTypeConverter());
        factory.getConverterFactory().registerConverter(new ScheduleConverter());
    }
}
