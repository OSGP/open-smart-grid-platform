// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class CoupleMbusDeviceResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = -6470713070003127394L;

  private final String mbusDeviceIdentification;
  private final ChannelElementValuesDto channelElementValues;

  public CoupleMbusDeviceResponseDto(
      final String mbusDeviceIdentification, final ChannelElementValuesDto channelElementValues) {
    this.mbusDeviceIdentification = mbusDeviceIdentification;
    this.channelElementValues = channelElementValues;
  }

  public String getMbusDeviceIdentification() {
    return this.mbusDeviceIdentification;
  }

  public ChannelElementValuesDto getChannelElementValues() {
    return this.channelElementValues;
  }
}
