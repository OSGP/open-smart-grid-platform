package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeReadsRequest;
import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.SynchronizeTimeReads;

@Component(value = "adhocMapper")
public class AdhocMapper extends ConfigurableMapper {
    @Override
    public void configure(final MapperFactory mapperFactory) {

    	// entity SynchronizeTimeReads -> WS SynchronizeTimeReads
        mapperFactory
        .classMap(SynchronizeTimeReads.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeReads.class)
                .byDefault().register();

        mapperFactory
        .classMap(SynchronizeTimeReadsRequest.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeReadsRequest.class)
                .byDefault().register();    	
        
    }

}
