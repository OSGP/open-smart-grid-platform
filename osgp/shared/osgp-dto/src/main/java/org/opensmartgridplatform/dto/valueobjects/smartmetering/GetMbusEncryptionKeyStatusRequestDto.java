/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class GetMbusEncryptionKeyStatusRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = 3432576706197401825L;

  private String mbusDeviceIdentification;
  private Short channel;

  public GetMbusEncryptionKeyStatusRequestDto(
      final String mbusDeviceIdentification, final Short channel) {
    this.mbusDeviceIdentification = mbusDeviceIdentification;
    this.channel = channel;
  }

  public String getMbusDeviceIdentification() {
    return this.mbusDeviceIdentification;
  }

  public Short getChannel() {
    return this.channel;
  }
}
