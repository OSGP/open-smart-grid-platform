// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class GetConfigurationObjectResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = 4779593744529504288L;

  private final ConfigurationObjectDto configurationObject;

  public GetConfigurationObjectResponseDto(final ConfigurationObjectDto configurationObject) {
    this.configurationObject = configurationObject;
  }

  public ConfigurationObjectDto getConfigurationObject() {
    return this.configurationObject;
  }
}
