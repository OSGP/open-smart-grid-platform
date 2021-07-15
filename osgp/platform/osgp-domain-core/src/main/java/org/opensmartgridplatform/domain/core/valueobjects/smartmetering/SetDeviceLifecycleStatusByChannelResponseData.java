/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;

public class SetDeviceLifecycleStatusByChannelResponseData extends ActionResponse {

  private static final long serialVersionUID = 3636769765482239443L;

  private final String gatewayDeviceIdentification;
  private final short channel;
  private final String mbusDeviceIdentification;
  private final DeviceLifecycleStatus deviceLifecycleStatus;

  public SetDeviceLifecycleStatusByChannelResponseData(
      final String gatewayDeviceIdentification,
      final short channel,
      final String mbusDeviceIdentification,
      final DeviceLifecycleStatus deviceLifecycleStatus) {
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

  public DeviceLifecycleStatus getDeviceLifecycleStatus() {
    return this.deviceLifecycleStatus;
  }
}
