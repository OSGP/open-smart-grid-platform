// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class CoupleMbusDeviceByChannelRequestDataDto implements ActionRequestDto {

  private static final long serialVersionUID = -3950523153973146555L;

  private final short channel;

  public CoupleMbusDeviceByChannelRequestDataDto(final short channel) {
    this.channel = channel;
  }

  public short getChannel() {
    return this.channel;
  }
}
