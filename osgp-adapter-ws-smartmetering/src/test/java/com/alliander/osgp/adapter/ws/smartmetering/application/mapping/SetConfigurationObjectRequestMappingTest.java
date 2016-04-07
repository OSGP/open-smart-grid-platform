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
import static org.junit.Assert.assertNull;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlag;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlagType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationFlags;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ConfigurationObject;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GprsOperationModeType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequestData;

public class SetConfigurationObjectRequestMappingTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    // Test if mapping succeeds when SetConfigurationObjectRequestData is null
    @Test
    public void testWithNullSetConfigurationObjectRequestData() {
        // build test data
        final String deviceIdentification = "nr1";
        final SetConfigurationObjectRequestData setConfigurationObjectRequestData = null;
        final SetConfigurationObjectRequest requestOriginal = new SetConfigurationObjectRequest();
        requestOriginal.setDeviceIdentification(deviceIdentification);
        requestOriginal.setSetConfigurationObjectRequestData(setConfigurationObjectRequestData);

        // actual mapping
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest requestMapped = this.mapperFactory
                .getMapperFacade().map(requestOriginal,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest.class);

        // check mapping
        assertNotNull(requestMapped);
        assertEquals(deviceIdentification, requestMapped.getDeviceIdentification());
        assertNull(requestMapped.getSetConfigurationObjectRequestData());
    }

    // Test if mapping succeeds when ConfigurationObject is null
    @Test
    public void testWithNullConfigurationObject() {
        // build test data
        final String deviceIdentification = "nr1";
        final ConfigurationObject configurationObject = null;
        final SetConfigurationObjectRequestData setConfigurationObjectRequestData = new SetConfigurationObjectRequestData();
        setConfigurationObjectRequestData.setConfigurationObject(configurationObject);
        final SetConfigurationObjectRequest requestOriginal = new SetConfigurationObjectRequest();
        requestOriginal.setDeviceIdentification(deviceIdentification);
        requestOriginal.setSetConfigurationObjectRequestData(setConfigurationObjectRequestData);

        // actual mapping
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest requestMapped = this.mapperFactory
                .getMapperFacade().map(requestOriginal,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest.class);

        // check mapping
        assertNotNull(requestMapped);
        assertEquals(deviceIdentification, requestMapped.getDeviceIdentification());
        assertNotNull(requestMapped.getSetConfigurationObjectRequestData());
        assertNull(requestMapped.getSetConfigurationObjectRequestData().getConfigurationObject());
    }

    // Test if mapping succeeds with a complete SetConfigurationRequestData
    // object.
    @Test
    public void testWithCompleteObject() {
        // build test data
        final String deviceIdentification = "nr1";
        final ConfigurationObject configurationObject = new ConfigurationObject();
        final ConfigurationFlag configurationFlag = new ConfigurationFlag();
        configurationFlag.setConfigurationFlagType(ConfigurationFlagType.DISCOVER_ON_OPEN_COVER);
        configurationFlag.setEnabled(true);
        final ConfigurationFlags configurationFlags = new ConfigurationFlags();
        configurationFlags.getConfigurationFlag().add(configurationFlag);
        configurationObject.setConfigurationFlags(configurationFlags);
        configurationObject.setGprsOperationMode(GprsOperationModeType.ALWAYS_ON);
        final SetConfigurationObjectRequestData setConfigurationObjectRequestData = new SetConfigurationObjectRequestData();
        setConfigurationObjectRequestData.setConfigurationObject(configurationObject);
        final SetConfigurationObjectRequest requestOriginal = new SetConfigurationObjectRequest();
        requestOriginal.setDeviceIdentification(deviceIdentification);
        requestOriginal.setSetConfigurationObjectRequestData(setConfigurationObjectRequestData);

        // actual mapping
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest requestMapped = this.mapperFactory
                .getMapperFacade().map(requestOriginal,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest.class);

        // check mapping
        assertNotNull(requestMapped);
        assertEquals(deviceIdentification, requestMapped.getDeviceIdentification());
        assertNotNull(requestMapped.getSetConfigurationObjectRequestData());
        assertEquals(configurationObject.getGprsOperationMode().name(), requestMapped
                .getSetConfigurationObjectRequestData().getConfigurationObject().getGprsOperationMode().name());
        assertEquals(configurationObject.getConfigurationFlags().getConfigurationFlag().get(0)
                .getConfigurationFlagType().name(), requestMapped.getSetConfigurationObjectRequestData()
                .getConfigurationObject().getConfigurationFlags().getConfigurationFlag().get(0)
                .getConfigurationFlagType().name());

    }

}
