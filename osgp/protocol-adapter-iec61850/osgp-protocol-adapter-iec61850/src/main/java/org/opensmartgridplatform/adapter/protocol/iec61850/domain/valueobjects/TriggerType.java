/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
