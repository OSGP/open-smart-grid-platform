// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SetThdConfigurationRequestDto implements ActionRequestDto {

  private final ThdConfigurationDto thdConfiguration;

  public SetThdConfigurationRequestDto(final ThdConfigurationDto thdConfiguration) {
    this.thdConfiguration = thdConfiguration;
  }

  public ThdConfigurationDto getThdConfiguration() {
    return this.thdConfiguration;
  }
}
