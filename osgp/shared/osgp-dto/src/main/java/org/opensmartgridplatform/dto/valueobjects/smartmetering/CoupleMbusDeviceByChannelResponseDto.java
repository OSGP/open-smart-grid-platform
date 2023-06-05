// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class CoupleMbusDeviceByChannelResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = -6470713070003127394L;

  private ChannelElementValuesDto channelElementValues;

  public CoupleMbusDeviceByChannelResponseDto(final ChannelElementValuesDto channelElementValues) {
    this.channelElementValues = channelElementValues;
  }

  public ChannelElementValuesDto getChannelElementValues() {
    return this.channelElementValues;
  }
}
