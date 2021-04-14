/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects;

/** The RelayType indexing, used by the Device */
public enum DeviceRelayType {
  TARIFF(0),
  LIGHT(1);

  private int index;

  private DeviceRelayType(final int index) {
    this.index = index;
  }

  public static DeviceRelayType getByIndex(final int index) {

    for (final DeviceRelayType deviceRelayType : values()) {
      if (deviceRelayType.index == index) {
        return deviceRelayType;
      }
    }

    throw new IllegalArgumentException(String.valueOf(index));
  }

  public int getIndex() {
    return this.index;
  }
}
