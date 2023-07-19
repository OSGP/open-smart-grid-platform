// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import lombok.Data;

@Data
public class CoupleMbusDeviceRequestDataDto implements ActionRequestDto {

  private static final long serialVersionUID = -3950523153973146555L;

  private String mbusDeviceIdentification;

  private boolean force;

  private final MbusChannelElementsDto mbusChannelElements;

  public CoupleMbusDeviceRequestDataDto(
      final String mbusDeviceIdentification,
      final boolean force,
      final MbusChannelElementsDto mbusChannelElements) {
    this.mbusDeviceIdentification = mbusDeviceIdentification;
    this.force = force;
    this.mbusChannelElements = mbusChannelElements;
  }
}
