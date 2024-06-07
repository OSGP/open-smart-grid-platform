// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serial;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DecoupleMbusDeviceResponseDto extends ActionResponseDto {

  @Serial private static final long serialVersionUID = -4454979905929290745L;

  private static final String SUCCESSFUL_RESULT = "Decouple Mbus Device was successful";

  @Setter private String mbusDeviceIdentification;
  private final ChannelElementValuesDto channelElementValues;

  public DecoupleMbusDeviceResponseDto(final ChannelElementValuesDto channelElementValues) {
    super(SUCCESSFUL_RESULT);
    this.channelElementValues = channelElementValues;
  }

  public DecoupleMbusDeviceResponseDto(
      final boolean success, final ChannelElementValuesDto channelElementValues) {
    super(
        success
            ? SUCCESSFUL_RESULT
            : "Channel information could not be correctly interpreted. "
                + "Mbus Device was successful decoupled anyway.");
    this.channelElementValues = channelElementValues;
  }

  @Override
  public String toString() {
    return String.format(
        "DecoupleMbusDeviceResponseDto [channel=%d, mbusDeviceIdentification=%s]",
        this.channelElementValues.getChannel(), this.mbusDeviceIdentification);
  }
}
