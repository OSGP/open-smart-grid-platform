/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;

public class AdministrativeStatusTypeMappingTest {

    private ConfigurationMapper configurationMapper = new ConfigurationMapper();

    // To see if mapping succeeds when a value is set to undefined.
    @Test
    public void testForValueUndefined() {

        // build test data
        final AdministrativeStatusType administrativeStatusType = AdministrativeStatusType.UNDEFINED;

        // actual mapping
        final AdministrativeStatusTypeDto administrativeStatusTypeDto = this.configurationMapper.map(
                administrativeStatusType, AdministrativeStatusTypeDto.class);

        // check if value is mapped correctly
        assertNotNull(administrativeStatusTypeDto);
        assertEquals(administrativeStatusType.name(), administrativeStatusTypeDto.name());
    }

    // To see if mapping succeeds when a value is set to On.
    @Test
    public void testForValueOn() {

        // build test data
        final AdministrativeStatusType administrativeStatusType = AdministrativeStatusType.ON;

        // actual mapping
        final AdministrativeStatusTypeDto administrativeStatusTypeDto = this.configurationMapper.map(
                administrativeStatusType, AdministrativeStatusTypeDto.class);

        // check if value is mapped correctly
        assertNotNull(administrativeStatusTypeDto);
        assertEquals(administrativeStatusType.name(), administrativeStatusTypeDto.name());
    }

    // To see if mapping succeeds when a value is set to Off.
    @Test
    public void testForValueOff() {

        // build test data
        final AdministrativeStatusType administrativeStatusType = AdministrativeStatusType.OFF;

        // actual mapping
        final AdministrativeStatusTypeDto administrativeStatusTypeDto = this.configurationMapper.map(
                administrativeStatusType, AdministrativeStatusTypeDto.class);

        // check if value is mapped correctly
        assertNotNull(administrativeStatusTypeDto);
        assertEquals(administrativeStatusType.name(), administrativeStatusTypeDto.name());
    }

    // check if mapping succeeds if the object is null.
    @Test
    public void testNull() {

        // build test data
        final AdministrativeStatusType administrativeStatusType = null;

        // actual mapping
        final AdministrativeStatusTypeDto administrativeStatusTypeDto = this.configurationMapper.map(
                administrativeStatusType, AdministrativeStatusTypeDto.class);

        // check if value is mapped correctly
        assertNull(administrativeStatusTypeDto);
    }

}
