// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects;

/** The timeout type, used by the Device schedule */
public enum TriggerType {
  FIX(0),
  SENSOR(1),
  AUTONOME(2);

  private int index;

  private TriggerType(final int index) {
    this.index = index;
  }

  public static TriggerType getByIndex(final int index) {
    for (final TriggerType triggerType : values()) {
      if (triggerType.index == index) {
        return triggerType;
      }
    }

    throw new IllegalArgumentException(String.valueOf(index));
  }

  public int getIndex() {
    return this.index;
  }
}
