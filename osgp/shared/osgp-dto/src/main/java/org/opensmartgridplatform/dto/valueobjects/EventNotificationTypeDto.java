// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects;

public enum EventNotificationTypeDto implements java.io.Serializable {
  DIAG_EVENTS(1),
  HARDWARE_FAILURE(2),
  LIGHT_EVENTS(4),
  TARIFF_EVENTS(8),
  MONITOR_EVENTS(16),
  FIRMWARE_EVENTS(32),
  COMM_EVENTS(64),
  SECURITY_EVENTS(128);

  private int value;

  EventNotificationTypeDto(final int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }
}
