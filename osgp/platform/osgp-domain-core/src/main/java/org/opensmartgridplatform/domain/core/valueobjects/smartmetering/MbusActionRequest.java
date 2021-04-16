/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

public abstract class MbusActionRequest implements ActionRequest {

  private static final long serialVersionUID = 2320272681064473304L;

  private String mbusDeviceIdentification;
  private Short channel;

  public MbusActionRequest(final String mbusDeviceIdentification) {
    this.mbusDeviceIdentification = mbusDeviceIdentification;
  }

  public String getMbusDeviceIdentification() {
    return this.mbusDeviceIdentification;
  }

  public Short getChannel() {
    return this.channel;
  }

  public void setChannel(final Short channel) {
    this.channel = channel;
  }
}
