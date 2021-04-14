/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

public enum EventNotificationType implements java.io.Serializable {
  DIAG_EVENTS(1),
  HARDWARE_FAILURE(2),
  LIGHT_EVENTS(4),
  TARIFF_EVENTS(8),
  MONITOR_EVENTS(16),
  FIRMWARE_EVENTS(32),
  COMM_EVENTS(64),
  SECURITY_EVENTS(128);

  private int value;

  EventNotificationType(final int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }
}
