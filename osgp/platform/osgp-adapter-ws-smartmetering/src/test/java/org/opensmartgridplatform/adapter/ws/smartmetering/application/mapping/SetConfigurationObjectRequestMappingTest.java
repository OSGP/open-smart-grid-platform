/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationFlag;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationFlagType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationFlags;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigurationObject;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GprsOperationModeType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequestData;

public class SetConfigurationObjectRequestMappingTest {

    private ConfigurationMapper configurationMapper = new ConfigurationMapper();
    private static final String DEVICE_ID = "id1";
    private static final ConfigurationFlagType FLAGTYPE = ConfigurationFlagType.DISCOVER_ON_OPEN_COVER;
    private static final GprsOperationModeType GPRSTYPE = GprsOperationModeType.ALWAYS_ON;
    private static final boolean ISENABLED = true;

    /**
     * Tests if mapping succeeds when SetConfigurationObjectRequestData is null.
     */
    @Test
    public void testWithNullSetConfigurationObjectRequestData() {

        // build test data
        final SetConfigurationObjectRequestData setConfigurationObjectRequestData = null;
        final SetConfigurationObjectRequest requestOriginal = new SetConfigurationObjectRequest();
        requestOriginal.setDeviceIdentification(DEVICE_ID);
        requestOriginal.setSetConfigurationObjectRequestData(setConfigurationObjectRequestData);

        // actual mapping
        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest requestMapped = this.configurationMapper
                .map(requestOriginal,
                        org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest.class);

        // check mapping
        assertNotNull(requestMapped);
        assertNotNull(requestMapped.getDeviceIdentification());
        assertEquals(DEVICE_ID, requestMapped.getDeviceIdentification());
        assertNull(requestMapped.getSetConfigurationObjectRequestData());
    }

    /**
     * Tests if mapping succeeds when ConfigurationObject is null.
     */
    @Test
    public void testWithNullConfigurationObject() {
        // build test data
        final ConfigurationObject configurationObject = null;
        final SetConfigurationObjectRequestData setConfigurationObjectRequestData = new SetConfigurationObjectRequestData();
        setConfigurationObjectRequestData.setConfigurationObject(configurationObject);
        final SetConfigurationObjectRequest requestOriginal = new SetConfigurationObjectRequest();
        requestOriginal.setDeviceIdentification(DEVICE_ID);
        requestOriginal.setSetConfigurationObjectRequestData(setConfigurationObjectRequestData);

        // actual mapping
        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest requestMapped = this.configurationMapper
                .map(requestOriginal,
                        org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest.class);

        // check mapping
        assertNotNull(requestMapped);
        assertNotNull(requestMapped.getDeviceIdentification());
        assertEquals(DEVICE_ID, requestMapped.getDeviceIdentification());
        assertNotNull(requestMapped.getSetConfigurationObjectRequestData());
        assertNull(requestMapped.getSetConfigurationObjectRequestData().getConfigurationObject());
    }

    /**
     * Tests if mapping succeeds with a complete SetConfigurationRequestData
     * object.
     */
    @Test
    public void testWithCompleteObject() {

        // build test data
        final ConfigurationObject configurationObject = new ConfigurationObject();
        final ConfigurationFlag configurationFlag = new ConfigurationFlag();
        configurationFlag.setConfigurationFlagType(FLAGTYPE);
        configurationFlag.setEnabled(ISENABLED);
        final ConfigurationFlags configurationFlags = new ConfigurationFlags();
        configurationFlags.getConfigurationFlag().add(configurationFlag);
        configurationObject.setConfigurationFlags(configurationFlags);
        configurationObject.setGprsOperationMode(GPRSTYPE);
        final SetConfigurationObjectRequestData setConfigurationObjectRequestData = new SetConfigurationObjectRequestData();
        setConfigurationObjectRequestData.setConfigurationObject(configurationObject);
        final SetConfigurationObjectRequest requestOriginal = new SetConfigurationObjectRequest();
        requestOriginal.setDeviceIdentification(DEVICE_ID);
        requestOriginal.setSetConfigurationObjectRequestData(setConfigurationObjectRequestData);

        // actual mapping
        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest requestMapped = this.configurationMapper
                .map(requestOriginal,
                        org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest.class);

        // check mapping
        assertNotNull(requestMapped);
        assertEquals(DEVICE_ID, requestMapped.getDeviceIdentification());
        assertNotNull(requestMapped.getSetConfigurationObjectRequestData());
        assertNotNull(requestMapped.getSetConfigurationObjectRequestData().getConfigurationObject());
        assertNotNull(requestMapped.getSetConfigurationObjectRequestData().getConfigurationObject()
                .getGprsOperationMode());
        assertNotNull(requestMapped.getSetConfigurationObjectRequestData().getConfigurationObject()
                .getConfigurationFlags());
        assertNotNull(requestMapped.getSetConfigurationObjectRequestData().getConfigurationObject()
                .getConfigurationFlags().getConfigurationFlag());
        assertNotNull(requestMapped.getSetConfigurationObjectRequestData().getConfigurationObject()
                .getConfigurationFlags().getConfigurationFlag().get(0));
        assertNotNull(requestMapped.getSetConfigurationObjectRequestData().getConfigurationObject()
                .getConfigurationFlags().getConfigurationFlag().get(0).getConfigurationFlagType());
        assertEquals(GPRSTYPE.name(), requestMapped.getSetConfigurationObjectRequestData().getConfigurationObject()
                .getGprsOperationMode().name());
        assertEquals(FLAGTYPE.name(), requestMapped.getSetConfigurationObjectRequestData().getConfigurationObject()
                .getConfigurationFlags().getConfigurationFlag().get(0).getConfigurationFlagType().name());

    }

}
