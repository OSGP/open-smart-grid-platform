/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.domain.valueobjects;

public class EventNotificationToBeSent {
  private final Long deviceId;
  private final Boolean lightOn;

  public EventNotificationToBeSent(final Long deviceId, final Boolean lightOn) {
    this.deviceId = deviceId;
    this.lightOn = lightOn;
  }

  public Long getdeviceId() {
    return this.deviceId;
  }

  public Boolean getLightOn() {
    return this.lightOn;
  }
}
