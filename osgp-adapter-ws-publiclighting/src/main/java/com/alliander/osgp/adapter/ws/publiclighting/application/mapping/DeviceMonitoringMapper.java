package com.alliander.osgp.adapter.ws.publiclighting.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.publiclighting.application.mapping.ws.HistoryTermTypeConverter;
import com.alliander.osgp.adapter.ws.publiclighting.application.mapping.ws.PowerUsageMeterTypeConverter;
import com.alliander.osgp.shared.mappers.XMLGregorianCalendarToDateTimeConverter;

@Component(value = "publicLightingDeviceMonitoringMapper")
public class DeviceMonitoringMapper extends ConfigurableMapper {

    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.getConverterFactory().registerConverter(new PowerUsageMeterTypeConverter());
        mapperFactory.getConverterFactory().registerConverter(new HistoryTermTypeConverter());
        mapperFactory.getConverterFactory().registerConverter(new XMLGregorianCalendarToDateTimeConverter());
    }
}
