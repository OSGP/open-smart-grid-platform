/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ConfigurationFlag;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ConfigurationObject;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GetConfigurationObjectResponse;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.GprsOperationModeType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetConfigurationObjectResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GprsOperationModeTypeDto;

public class GetConfigurationObjectResponseMappingTest {

    private ConfigurationMapper mapper = new ConfigurationMapper();

    @Test
    public void testMapGetConfigurationObjectResponseDto() {
        final GetConfigurationObjectResponseDto dto = this.makeGetConfigurationObjectResponseDto();
        final GetConfigurationObjectResponse result = this.mapper.map(dto, GetConfigurationObjectResponse.class);
        assertNotNull("mapping GetConfigurationObjectResponseDto should not return null", result);
        assertThat("mapping GetConfigurationObjectResponseDto should return correct type", result,
                instanceOf(GetConfigurationObjectResponse.class));
    }

    @Test
    public void testMapConfigurationFlagDto() {
        final ConfigurationFlagDto dto = this.makeConfigurationFlagDto();
        final ConfigurationFlag result = this.mapper.map(dto, ConfigurationFlag.class);
        assertNotNull("mapping ConfigurationFlagDto should not return null", result);
        assertThat("mapping ConfigurationFlagDto should return correct type", result,
                instanceOf(ConfigurationFlag.class));
    }

    @Test
    public void testMapGprsOperationModeTypeDto() {
        final GprsOperationModeTypeDto dto = GprsOperationModeTypeDto.ALWAYS_ON;
        final GprsOperationModeType result = this.mapper.map(dto, GprsOperationModeType.class);
        assertNotNull("mapping GprsOperationModeTypeDto should not return null", result);
        assertThat("mapping GprsOperationModeTypeDto should return correct type", result,
                instanceOf(GprsOperationModeType.class));
    }

    @Test
    public void testMapConfigurationObjectDto() {
        final ConfigurationObjectDto dto = this.makeConfigurationObjectDto();
        final ConfigurationObject result = this.mapper.map(dto, ConfigurationObject.class);
        assertNotNull("mapping ConfigurationObjectDto should not return null", result);
        assertThat("mapping ConfigurationObjectDto should return correct type", result,
                instanceOf(ConfigurationObject.class));
    }

    private GetConfigurationObjectResponseDto makeGetConfigurationObjectResponseDto() {
        final ConfigurationObjectDto configObjectDto = this.makeConfigurationObjectDto();
        return new GetConfigurationObjectResponseDto(configObjectDto);
    }

    private ConfigurationObjectDto makeConfigurationObjectDto() {
        final List<ConfigurationFlagDto> configurationFlags = new ArrayList<>();
        configurationFlags.add(this.makeConfigurationFlagDto());
        final ConfigurationFlagsDto flags = new ConfigurationFlagsDto(configurationFlags);
        final ConfigurationObjectDto configObjectDto = new ConfigurationObjectDto(GprsOperationModeTypeDto.ALWAYS_ON,
                flags);
        return configObjectDto;
    }

    private ConfigurationFlagDto makeConfigurationFlagDto() {
        return new ConfigurationFlagDto(ConfigurationFlagTypeDto.DISCOVER_ON_OPEN_COVER, true);
    }

}
