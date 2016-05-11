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

import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType;

public class AdministrativeStatusTypeMappingTest {

    private ConfigurationMapper configurationMapper = new ConfigurationMapper();

    /**
     * Both objects have the same name, but are in different packages. Mapping
     * needs to be bidirectional, so this tests mapping one way.
     */
    @Test
    public void testMappingOneWay() {

        // actual mapping
        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType undefined = this.configurationMapper
                .map(AdministrativeStatusType.UNDEFINED,
                        com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.class);
        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType off = this.configurationMapper
                .map(AdministrativeStatusType.OFF,
                        com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.class);
        final com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType on = this.configurationMapper
                .map(AdministrativeStatusType.ON,
                        com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.class);

        // check mapping
        assertNotNull(undefined);
        assertNotNull(off);
        assertNotNull(on);

        assertEquals(AdministrativeStatusType.UNDEFINED.name(), undefined.name());
        assertEquals(AdministrativeStatusType.OFF.name(), off.name());
        assertEquals(AdministrativeStatusType.ON.name(), on.name());
    }

    /**
     * Both objects have the same name, but are in different packages. Mapping
     * needs to be bidirectional, so this tests mapping the other way round.
     */
    @Test
    public void testMappingTheOtherWay() {

        // actual mapping
        final AdministrativeStatusType undefined = this.configurationMapper.map(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.UNDEFINED,
                AdministrativeStatusType.class);
        final AdministrativeStatusType off = this.configurationMapper.map(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.OFF,
                AdministrativeStatusType.class);
        final AdministrativeStatusType on = this.configurationMapper.map(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.ON,
                AdministrativeStatusType.class);

        // check mapping
        assertNotNull(undefined);
        assertNotNull(off);
        assertNotNull(on);

        assertEquals(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.UNDEFINED
                .name(),
                undefined.name());
        assertEquals(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.OFF.name(),
                off.name());
        assertEquals(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.ON.name(),
                on.name());
    }
}
