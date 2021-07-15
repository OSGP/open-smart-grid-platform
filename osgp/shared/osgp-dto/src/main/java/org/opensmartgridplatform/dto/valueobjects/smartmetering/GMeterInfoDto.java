/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

/** Class to pass G-Meter information to the protocol adapter */
public class GMeterInfoDto implements ActionRequestDto {

  private static final long serialVersionUID = -4321438772672309715L;
  private final int channel;
  private final String deviceIdentification;

  public GMeterInfoDto(final int channel, final String deviceIdentification) {
    this.channel = channel;
    this.deviceIdentification = deviceIdentification;
  }

  public int getChannel() {
    return this.channel;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }
}
