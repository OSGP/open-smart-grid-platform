package com.alliander.osgp.adapter.protocol.oslp.application.mapping.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.alliander.osgp.adapter.protocol.oslp.application.mapping.RelayTypeConverter;
import com.alliander.osgp.dto.valueobjects.RelayTypeDto;
import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.Oslp.RelayType;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public class RelayTypeConverterTest {

    private final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    @Test
    public void testMappingFromRelayTypeDtoToRelayType() {

        this.mapperFactory.getConverterFactory().registerConverter(new RelayTypeConverter());

        final RelayType relayType = this.mapperFactory.getMapperFacade().map(RelayTypeDto.TARIFF, Oslp.RelayType.class);

        assertNotNull(relayType);
        assertEquals(RelayType.TARIFF, relayType);
    }

}
