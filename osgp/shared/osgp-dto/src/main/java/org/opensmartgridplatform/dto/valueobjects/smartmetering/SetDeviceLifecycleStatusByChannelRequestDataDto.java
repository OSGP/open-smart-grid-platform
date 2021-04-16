/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
