/**
 * Copyright 2020 Alliander N.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public enum CircuitSwitchedStatusDto {
  INACTIVE(0),
  INCOMING_CALL(1),
  ACTIVE(2);

  private final int index;

  private CircuitSwitchedStatusDto(final int index) {
    this.index = index;
  }

  public int getIndex() {
    return this.index;
  }

  public static CircuitSwitchedStatusDto fromIndexValue(final int value) {
    for (final CircuitSwitchedStatusDto status : CircuitSwitchedStatusDto.values()) {
      if (status.index == value) {
        return status;
      }
    }
    return null;
  }

  public String value() {
    return this.name();
  }

  public static CircuitSwitchedStatusDto fromValue(final String v) {
    return valueOf(v);
  }
}
