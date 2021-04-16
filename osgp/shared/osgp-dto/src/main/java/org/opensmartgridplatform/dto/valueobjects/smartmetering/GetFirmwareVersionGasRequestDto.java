/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import lombok.Getter;

@Getter
public class GetFirmwareVersionGasRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = -5274875125208726393L;

  private final ChannelDto channel;

  private final String mbusDeviceIdentification;

  public GetFirmwareVersionGasRequestDto(
      final ChannelDto channel, final String mbusDeviceIdentification) {
    this.channel = channel;
    this.mbusDeviceIdentification = mbusDeviceIdentification;
  }
}
