// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SetDeviceLifecycleStatusByChannelResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = -2072098847524867862L;

  private final String gatewayDeviceIdentification;
  private final short channel;
  private final String mbusDeviceIdentification;
  private final DeviceLifecycleStatusDto deviceLifecycleStatus;

  public SetDeviceLifecycleStatusByChannelResponseDto(
      final String gatewayDeviceIdentification,
      final short channel,
      final String mbusDeviceIdentification,
      final DeviceLifecycleStatusDto deviceLifecycleStatus) {
    this.gatewayDeviceIdentification = gatewayDeviceIdentification;
    this.channel = channel;
    this.deviceLifecycleStatus = deviceLifecycleStatus;
    this.mbusDeviceIdentification = mbusDeviceIdentification;
  }

  public String getGatewayDeviceIdentification() {
    return this.gatewayDeviceIdentification;
  }

  public short getChannel() {
    return this.channel;
  }

  public String getMbusDeviceIdentification() {
    return this.mbusDeviceIdentification;
  }

  public DeviceLifecycleStatusDto getDeviceLifecycleStatus() {
    return this.deviceLifecycleStatus;
  }
}
