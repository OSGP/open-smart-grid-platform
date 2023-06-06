// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public enum ChannelDto {
  ONE(1),
  TWO(2),
  THREE(3),
  FOUR(4);

  private final int channelNumber;

  private ChannelDto(final int channelNumber) {
    this.channelNumber = channelNumber;
  }

  public int getChannelNumber() {
    return this.channelNumber;
  }

  public static ChannelDto fromNumber(final int channel) {
    switch (channel) {
      case 1:
        return ONE;
      case 2:
        return TWO;
      case 3:
        return THREE;
      case 4:
        return FOUR;
      default:
        throw new IllegalArgumentException(String.format("channel %s not supported", channel));
    }
  }
}
