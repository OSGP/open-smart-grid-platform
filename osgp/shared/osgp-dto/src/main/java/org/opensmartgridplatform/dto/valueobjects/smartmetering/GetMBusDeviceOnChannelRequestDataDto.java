// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class GetMBusDeviceOnChannelRequestDataDto implements Serializable, ActionRequestDto {

  private static final long serialVersionUID = -5720146325505371298L;

  private final String gatewayDeviceIdentification;
  private final short channel;

  public GetMBusDeviceOnChannelRequestDataDto(
      final String gatewayDeviceIdentification, final short channel) {
    this.gatewayDeviceIdentification = gatewayDeviceIdentification;
    this.channel = channel;
  }

  public String getGatewayDeviceIdentification() {
    return this.gatewayDeviceIdentification;
  }

  public short getChannel() {
    return this.channel;
  }
}
