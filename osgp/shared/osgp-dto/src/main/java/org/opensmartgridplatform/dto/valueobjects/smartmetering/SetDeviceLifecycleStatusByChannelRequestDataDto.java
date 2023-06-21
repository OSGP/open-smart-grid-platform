// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class SetDeviceLifecycleStatusByChannelRequestDataDto implements ActionRequestDto {

  private static final long serialVersionUID = 2705058095687339039L;

  private final short channel;
  private final DeviceLifecycleStatusDto deviceLifecycleStatus;

  public SetDeviceLifecycleStatusByChannelRequestDataDto(
      final short channel, final DeviceLifecycleStatusDto deviceLifecycleStatus) {
    this.channel = channel;
    this.deviceLifecycleStatus = deviceLifecycleStatus;
  }

  public short getChannel() {
    return this.channel;
  }

  public DeviceLifecycleStatusDto getDeviceLifecycleStatus() {
    return this.deviceLifecycleStatus;
  }
}
