/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
