/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.entities;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class DeviceKeyProcessing {

  @Id
  @Column(length = 40)
  private String deviceIdentification;

  @Column private Instant startTime;

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public void setDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
  }

  public Instant getStartTime() {
    return this.startTime;
  }

  public void setStartTime(final Instant startTime) {
    this.startTime = startTime;
  }
}
