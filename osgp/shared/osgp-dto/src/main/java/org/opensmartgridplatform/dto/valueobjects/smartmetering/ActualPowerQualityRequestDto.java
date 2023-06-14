// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class ActualPowerQualityRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = 1544769605230783612L;

  private final String profileType;

  public ActualPowerQualityRequestDto(final String profileType) {
    this.profileType = profileType;
  }

  public String getProfileType() {
    return this.profileType;
  }
}
