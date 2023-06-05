// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.domain.valueobjects;

public enum EventNotificationType {
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

  public String value() {
    return this.name();
  }

  public int getValue() {
    return this.value;
  }

  public static EventNotificationType fromValue(final String v) {
    return valueOf(v);
  }
}
