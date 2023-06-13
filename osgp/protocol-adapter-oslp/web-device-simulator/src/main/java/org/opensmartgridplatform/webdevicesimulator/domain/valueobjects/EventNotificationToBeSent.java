// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
