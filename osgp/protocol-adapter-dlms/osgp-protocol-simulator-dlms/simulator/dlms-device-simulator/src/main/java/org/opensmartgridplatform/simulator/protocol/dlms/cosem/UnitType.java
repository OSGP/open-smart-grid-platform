/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

/** Unit enumeration with integer values as defined in DLMS Blue book. */
public enum UnitType {
  DATA_VALID(0),
  DATA_NOT_VALID(4),
  CUBIC_METER(14),
  WATT(27),
  VAR(29),
  WATT_HOUR(30),
  AMPERE(33),
  VOLT(35),
  COUNT(255);

  private final int value;

  UnitType(final int v) {
    this.value = v;
  }

  public int value() {
    return this.value;
  }

  public static UnitType fromValue(final int v) {
    for (final UnitType c : UnitType.values()) {
      if (c.value == v) {
        return c;
      }
    }
    throw new IllegalArgumentException("UnitType does not exist: " + v);
  }
}
