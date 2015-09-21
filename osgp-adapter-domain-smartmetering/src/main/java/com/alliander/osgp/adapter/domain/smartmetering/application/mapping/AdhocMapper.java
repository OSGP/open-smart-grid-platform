package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

@Component(value = "adhocMapper")
public class AdhocMapper extends ConfigurableMapper {
	
	@Override
    public void configure(final MapperFactory mapperFactory) {
		
        // domain value object -> dto value object
        mapperFactory
        .classMap(com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeReadsRequest.class,
                com.alliander.osgp.dto.valueobjects.smartmetering.SynchronizeTimeReadsRequest.class).byDefault()
        .register();
        
    }
	
	
}
