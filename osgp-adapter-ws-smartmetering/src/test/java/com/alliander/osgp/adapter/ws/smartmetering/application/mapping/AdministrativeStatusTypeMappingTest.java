/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType;

public class AdministrativeStatusTypeMappingTest {

    // private ConfigurationMapper configurationMapper = new
    // ConfigurationMapper();
    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    // Both objects have the same name, but are in different packages. This test
    // tests mapping one way.
    @Test
    public void testMappingOneWay() {

        // build test data, AdministrativeStatusType is an enum with three
        // possible values.
        final AdministrativeStatusType type1 = AdministrativeStatusType.UNDEFINED;
        final AdministrativeStatusType type2 = AdministrativeStatusType.OFF;
        final AdministrativeStatusType type3 = AdministrativeStatusType.ON;

        // actual mapping
        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType type1Mapped = this.mapperFactory
                .getMapperFacade()
                .map(type1,
                        com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.class);
        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType type2Mapped = this.mapperFactory
                .getMapperFacade()
                .map(type2,
                        com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.class);
        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType type3Mapped = this.mapperFactory
                .getMapperFacade()
                .map(type3,
                        com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.class);

        // check mapping
        assertNotNull(type1Mapped);
        assertNotNull(type2Mapped);
        assertNotNull(type3Mapped);

        assertEquals(type1.value(), type1Mapped.value());
        assertEquals(type2.value(), type2Mapped.value());
        assertEquals(type3.value(), type3Mapped.value());
    }

    // Both objects have the same name, but are in different packages. This test
    // tests mapping the other way around.
    @Test
    public void testMappingTheOtherWay() {
        // build test data, AdministrativeStatusType is an enum with three
        // possible values.
        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType type1 = com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.UNDEFINED;
        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType type2 = com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.OFF;
        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType type3 = com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.ON;

        // actual mapping
        final AdministrativeStatusType type1Mapped = this.mapperFactory.getMapperFacade().map(type1,
                AdministrativeStatusType.class);
        final AdministrativeStatusType type2Mapped = this.mapperFactory.getMapperFacade().map(type2,
                AdministrativeStatusType.class);
        final AdministrativeStatusType type3Mapped = this.mapperFactory.getMapperFacade().map(type3,
                AdministrativeStatusType.class);

        // check mapping
        assertNotNull(type1Mapped);
        assertNotNull(type2Mapped);
        assertNotNull(type3Mapped);

        assertEquals(type1.value(), type1Mapped.value());
        assertEquals(type2.value(), type2Mapped.value());
        assertEquals(type3.value(), type3Mapped.value());
    }
}
