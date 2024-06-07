// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import lombok.Getter;

@Getter
public class DecoupleMbusDeviceResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = -4454979905929290745L;

  private String mbusDeviceIdentification;
  private final ChannelElementValuesDto channelElementValues;

  public DecoupleMbusDeviceResponseDto(final ChannelElementValuesDto channelElementValues) {
    super("Decouple Mbus Device was successful");
    this.channelElementValues = channelElementValues;
  }

  public DecoupleMbusDeviceResponseDto(
      final boolean success, final ChannelElementValuesDto channelElementValues) {
    super(
        success
            ? "Decouple Mbus Device was successful."
            : "Channel information could not be correctly interpreted. "
                + "Mbus Device was successful decoupled anyway.");
    this.channelElementValues = channelElementValues;
  }

  @Override
  public String toString() {
    return "DecoupleMbusDeviceResponseDto [channel="
        + this.channelElementValues.getChannel()
        + ", mbusDeviceIdentification="
        + this.mbusDeviceIdentification
        + "]";
  }

  public void setMbusDeviceIdentification(final String mbusDeviceIdentification) {
    this.mbusDeviceIdentification = mbusDeviceIdentification;
  }
}
