//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
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

  private final ConfigurationMapper mapper = new ConfigurationMapper();

  @Test
  public void testMapGetConfigurationObjectResponseDto() {
    final GetConfigurationObjectResponseDto dto = this.makeGetConfigurationObjectResponseDto();
    final GetConfigurationObjectResponse result =
        this.mapper.map(dto, GetConfigurationObjectResponse.class);
    assertThat(result)
        .withFailMessage("mapping GetConfigurationObjectResponseDto should not return null")
        .isNotNull();
    assertThat(result)
        .withFailMessage("mapping GetConfigurationObjectResponseDto should return correct type")
        .isOfAnyClassIn(GetConfigurationObjectResponse.class);
  }

  @Test
  public void testMapConfigurationFlagDto() {
    final ConfigurationFlagDto dto = this.makeConfigurationFlagDto();
    final ConfigurationFlag result = this.mapper.map(dto, ConfigurationFlag.class);
    assertThat(result)
        .withFailMessage("mapping ConfigurationFlagDto should not return null")
        .isNotNull();
    assertThat(result)
        .withFailMessage("mapping ConfigurationFlagDto should return correct type")
        .isOfAnyClassIn(ConfigurationFlag.class);
  }

  @Test
  public void testMapGprsOperationModeTypeDto() {
    final GprsOperationModeTypeDto dto = GprsOperationModeTypeDto.ALWAYS_ON;
    final GprsOperationModeType result = this.mapper.map(dto, GprsOperationModeType.class);
    assertThat(result)
        .withFailMessage("mapping GprsOperationModeTypeDto should not return null")
        .isNotNull();
    assertThat(result)
        .withFailMessage("mapping GprsOperationModeTypeDto should return correct type")
        .isOfAnyClassIn(GprsOperationModeType.class);
  }

  @Test
  public void testMapConfigurationObjectDto() {
    final ConfigurationObjectDto dto = this.makeConfigurationObjectDto();
    final ConfigurationObject result = this.mapper.map(dto, ConfigurationObject.class);
    assertThat(result)
        .withFailMessage("mapping ConfigurationObjectDto should not return null")
        .isNotNull();
    assertThat(result)
        .withFailMessage("mapping ConfigurationObjectDto should return correct type")
        .isOfAnyClassIn(ConfigurationObject.class);
  }

  private GetConfigurationObjectResponseDto makeGetConfigurationObjectResponseDto() {
    final ConfigurationObjectDto configObjectDto = this.makeConfigurationObjectDto();
    return new GetConfigurationObjectResponseDto(configObjectDto);
  }

  private ConfigurationObjectDto makeConfigurationObjectDto() {
    final List<ConfigurationFlagDto> configurationFlags = new ArrayList<>();
    configurationFlags.add(this.makeConfigurationFlagDto());
    final ConfigurationFlagsDto flags = new ConfigurationFlagsDto(configurationFlags);
    final ConfigurationObjectDto configObjectDto =
        new ConfigurationObjectDto(GprsOperationModeTypeDto.ALWAYS_ON, flags);
    return configObjectDto;
  }

  private ConfigurationFlagDto makeConfigurationFlagDto() {
    return new ConfigurationFlagDto(ConfigurationFlagTypeDto.DISCOVER_ON_OPEN_COVER, true);
  }
}
