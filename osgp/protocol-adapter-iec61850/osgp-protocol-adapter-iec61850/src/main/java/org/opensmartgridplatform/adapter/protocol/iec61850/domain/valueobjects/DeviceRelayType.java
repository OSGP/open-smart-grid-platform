// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
