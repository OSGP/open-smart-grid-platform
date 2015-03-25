package com.alliander.osgp.adapter.ws.admin.application.exceptionhandling;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

public class SoapFaultMapper extends ConfigurableMapper {
    @Override
    protected void configure(final MapperFactory factory) {
        factory.getConverterFactory().registerConverter(new FunctionalExceptionConverter());
        factory.getConverterFactory().registerConverter(new TechnicalExceptionConverter());
    }
}
