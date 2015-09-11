/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.metadata.Type;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.stereotype.Component;

@Component
public class OslpMapper extends ConfigurableMapper {

    private static final String TIME_FORMAT = "yyyyMMddHHmmss";

    @Override
    protected void configure(final MapperFactory factory) {
        factory.getConverterFactory().registerConverter(new IntegerToByteStringConverter());
        factory.getConverterFactory().registerConverter(new ConfigurationToOslpSetConfigurationRequestConverter());
        factory.getConverterFactory().registerConverter(new OslpGetConfigurationResponseToConfigurationConverter());
        factory.getConverterFactory().registerConverter(new DaliConfigurationToOslpDaliConfigurationConverter());
        factory.getConverterFactory().registerConverter(new RelayConfigurationToOslpRelayConfigurationConverter());
        factory.getConverterFactory().registerConverter(new LightTypeConverter());
        factory.getConverterFactory().registerConverter(new LinkTypeConverter());
        factory.getConverterFactory().registerConverter(new MeterTypeConverter());
        factory.getConverterFactory().registerConverter(new RelayTypeConverter());
        factory.getConverterFactory().registerConverter(new RelayDataConverter());
        factory.getConverterFactory().registerConverter(new PowerUsageDataConverter());
        factory.getConverterFactory().registerConverter(new HistoryTermTypeConverter());

        // Converter from String to DateTime using the Oslp time format.
        factory.getConverterFactory().registerConverter(new CustomConverter<String, DateTime>() {

            @Override
            public DateTime convert(final String source, final Type<? extends DateTime> destinationType) {
                return DateTimeFormat.forPattern(TIME_FORMAT).withZoneUTC().parseDateTime(source);
            }
        });

        // Converter from DateTime to String using the Oslp time format.
        factory.getConverterFactory().registerConverter(new CustomConverter<DateTime, String>() {

            @Override
            public String convert(final DateTime source, final Type<? extends String> destinationType) {
                return source.toString(DateTimeFormat.forPattern(TIME_FORMAT).withZoneUTC());
            }
        });
    }
}
