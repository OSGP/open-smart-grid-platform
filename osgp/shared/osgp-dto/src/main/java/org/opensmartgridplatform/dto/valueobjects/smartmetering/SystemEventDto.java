/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;

public class SystemEventDto implements Serializable {

  private static final long serialVersionUID = -5389008513362783376L;

  private final String deviceIdentification;
  private final SystemEventTypeDto systemEventType;
  private final Date timestamp;
  private final String reason;

  public SystemEventDto(
      final String deviceIdentification,
      final SystemEventTypeDto systemEventType,
      final Date timestamp,
      final String reason) {
    this.deviceIdentification = deviceIdentification;
    this.systemEventType = systemEventType;
    this.timestamp = timestamp;
    this.reason = reason;
  }

  @Override
  public String toString() {
    return String.format(
        "SystemEvent[device=%s, systemEventType=%s]",
        this.deviceIdentification, this.systemEventType);
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public SystemEventTypeDto getSystemEventType() {
    return this.systemEventType;
  }

  public Date getTimestamp() {
    return this.timestamp;
  }

  public String getReason() {
    return this.reason;
  }
}
