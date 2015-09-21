package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.stereotype.Component;

@Component(value = "adhocMapper")
public class AdhocMapper extends ConfigurableMapper {
    @Override
    public void configure(final MapperFactory mapperFactory) {
//        mapperFactory
//                .classMap(PeriodicMeterData.class,
//                        com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterData.class).byDefault()
//                .register();
//
//        mapperFactory
//                .classMap(PeriodicMeterReadsRequest.class,
//                        com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequest.class)
//                .byDefault().register();
    }

}
