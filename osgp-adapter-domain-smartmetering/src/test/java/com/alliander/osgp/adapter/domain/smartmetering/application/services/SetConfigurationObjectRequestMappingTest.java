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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ConfigurationFlag;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ConfigurationFlagType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ConfigurationFlags;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ConfigurationObject;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GprsOperationModeType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequestData;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GprsOperationModeTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDto;

// Testing the mapping of ConfigurationObjectRequest objects in ConfigurationService.
public class SetConfigurationObjectRequestMappingTest {

    private ConfigurationMapper configurationMapper = new ConfigurationMapper();

    // Tests if mapping a SetConfigurationObjectRequest with a null
    // SetConfigurationObjectRequestData object succeeds.
    @Test
    public void testSetConfigurationObjectRequestMappingNullObject() {

        // build test data
        final String deviceIdentification = "nr1";
        final SetConfigurationObjectRequestDataDto setConfigurationObjectRequestData = null;

        // actual mapping
        final SetConfigurationObjectRequestDto setConfigurationObjectRequest = 
                new SetConfigurationObjectRequestDto(
                deviceIdentification, setConfigurationObjectRequestData);
        final SetConfigurationObjectRequestDto setConfigurationObjectRequestDto = this.configurationMapper.map(
                setConfigurationObjectRequest, SetConfigurationObjectRequestDto.class);

        // check values
        assertNotNull(setConfigurationObjectRequestDto);
        assertEquals(deviceIdentification, setConfigurationObjectRequestDto.getDeviceIdentification());
        assertNull(setConfigurationObjectRequestDto.getSetConfigurationObjectRequestData());
    }

    // Test if mapping with a null ConfigurationObject succeeds
    @Test
    public void testMappingWithNullConfigurationObject() {

        // build test data
        final String deviceIdentification = "nr1";

        final ConfigurationObjectDto configurationObject = null;
        final SetConfigurationObjectRequestDataDto setConfigurationObjectRequestData = new SetConfigurationObjectRequestDataDto(
                configurationObject);

        // actual mapping
        final SetConfigurationObjectRequestDto setConfigurationObjectRequest = 
                new SetConfigurationObjectRequestDto(
                deviceIdentification, setConfigurationObjectRequestData);
        final SetConfigurationObjectRequestDto setConfigurationObjectRequestDto = this.configurationMapper.map(
                setConfigurationObjectRequest, SetConfigurationObjectRequestDto.class);

        // check values
        assertNotNull(setConfigurationObjectRequestDto);
        assertEquals(deviceIdentification, setConfigurationObjectRequestDto.getDeviceIdentification());
        assertNull(setConfigurationObjectRequestDto.getSetConfigurationObjectRequestData().getConfigurationObject());

    }

    // Test if mapping with ConfigurationFlags with an empty list succeeds.
    @Test
    public void testMappingWithEmptyList() {

        // build test data
        final String deviceIdentification = "nr1";
        final GprsOperationModeType gprsOperationModeType = GprsOperationModeType.ALWAYS_ON;
        final ConfigurationFlags configurationFlags = new ConfigurationFlags(new ArrayList<ConfigurationFlag>());
        final ConfigurationObject configurationObject = new ConfigurationObject(gprsOperationModeType,
                configurationFlags);
        final SetConfigurationObjectRequestData setConfigurationObjectRequestData = new SetConfigurationObjectRequestData(
                configurationObject);

        // actual mapping
        final SetConfigurationObjectRequest setConfigurationObjectRequest = 
                new SetConfigurationObjectRequest(
                deviceIdentification, setConfigurationObjectRequestData);
        final SetConfigurationObjectRequestDto setConfigurationObjectRequestDto = this.configurationMapper.map(
                setConfigurationObjectRequest, SetConfigurationObjectRequestDto.class);

        // check values
        assertNotNull(setConfigurationObjectRequestDto);
        assertEquals(deviceIdentification, setConfigurationObjectRequestDto.getDeviceIdentification());

        final ConfigurationObjectDto configurationObjectDto = setConfigurationObjectRequestDto
                .getSetConfigurationObjectRequestData().getConfigurationObject();
        assertNotNull(configurationObjectDto);

        // Check if both configurationFlags instances have an empty list
        assertTrue(configurationObjectDto.getConfigurationFlags().getConfigurationFlag().isEmpty());
    }

    // Tests if mapping with a complete SetConfigurationObjectRequestData object
    // succeeds
    @Test
    public void testSetConfigurationObjectRequestMappingComplete() {

        // build test data
        final String deviceIdentification = "nr1";
        final GprsOperationModeType gprsOperationModeType = GprsOperationModeType.ALWAYS_ON;
        final ConfigurationFlagType configurationFlagType = ConfigurationFlagType.DISCOVER_ON_OPEN_COVER;
        final ConfigurationFlag configurationFlag = new ConfigurationFlag(configurationFlagType, true);
        final List<ConfigurationFlag> configurationFlagList = new ArrayList<>();
        configurationFlagList.add(configurationFlag);
        final ConfigurationFlags configurationFlags = new ConfigurationFlags(configurationFlagList);
        final ConfigurationObject configurationObject = new ConfigurationObject(gprsOperationModeType,
                configurationFlags);
        final SetConfigurationObjectRequestData setConfigurationObjectRequestData = new SetConfigurationObjectRequestData(
                configurationObject);

        // actual mapping
        final SetConfigurationObjectRequest setConfigurationObjectRequest = 
                new SetConfigurationObjectRequest(
                deviceIdentification, setConfigurationObjectRequestData);
        final SetConfigurationObjectRequestDto setConfigurationObjectRequestDto = this.configurationMapper.map(
                setConfigurationObjectRequest, SetConfigurationObjectRequestDto.class);

        // check values
        assertNotNull(setConfigurationObjectRequestDto);
        assertEquals(deviceIdentification, setConfigurationObjectRequestDto.getDeviceIdentification());
        this.checkSetConfigurationObjectRequestData(gprsOperationModeType, configurationFlagType, configurationFlags,
                setConfigurationObjectRequestDto);

    }

    // method to check values of all objects that are mapped when a
    // SetConfigurationObjectRequest is mapped.
    private void checkSetConfigurationObjectRequestData(final GprsOperationModeType gprsOperationModeType,
            final ConfigurationFlagType configurationFlagType, final ConfigurationFlags configurationFlags,
            final SetConfigurationObjectRequestDto setConfigurationObjectRequestDto) {

        // check if SetConfigurationObjectRequesDataDto object is present
        final SetConfigurationObjectRequestDataDto setConfigurationObjectRequestDataDto = setConfigurationObjectRequestDto
                .getSetConfigurationObjectRequestData();
        assertNotNull(setConfigurationObjectRequestDataDto);

        // check if ConfigurationObjectDto object is present
        final ConfigurationObjectDto configurationObjectDto = setConfigurationObjectRequestDataDto
                .getConfigurationObject();
        assertNotNull(configurationObjectDto);

        // check the GprsOperationModeTypeDto value
        final GprsOperationModeTypeDto gprsOperationModeTypeDto = configurationObjectDto.getGprsOperationMode();
        assertEquals(gprsOperationModeType.name(), gprsOperationModeTypeDto.name());

        // check if ConfigurationFlagsDto object is present, and if its List is
        // of an equal size.
        final ConfigurationFlagsDto configurationFlagsDto = configurationObjectDto.getConfigurationFlags();
        assertNotNull(configurationFlagsDto);
        assertEquals(configurationFlags.getConfigurationFlag().size(), configurationFlagsDto.getConfigurationFlag()
                .size());

        // check ConfigurationObjectFlagTypeDto value.
        final ConfigurationFlagDto configurationFlagDto = configurationFlagsDto.getConfigurationFlag().get(0);
        final ConfigurationFlagTypeDto configurationFlagTypeDto = configurationFlagDto.getConfigurationFlagType();
        assertEquals(configurationFlagType.name(), configurationFlagTypeDto.name());
    }

}
