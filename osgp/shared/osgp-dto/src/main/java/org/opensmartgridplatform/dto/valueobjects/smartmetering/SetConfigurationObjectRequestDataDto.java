//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SetConfigurationObjectRequestDataDto implements ActionRequestDto {

  private static final long serialVersionUID = -381163520662276868L;

  private final ConfigurationObjectDto configurationObject;

  public SetConfigurationObjectRequestDataDto(final ConfigurationObjectDto configurationObject) {
    this.configurationObject = configurationObject;
  }

  public ConfigurationObjectDto getConfigurationObject() {
    return this.configurationObject;
  }
}
