/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec60870;

public class Iec60870InformationObject {

  private final int address;
  private final Iec60870InformationObjectType type;
  private final Object value;

  public Iec60870InformationObject(
      final int address, final Iec60870InformationObjectType type, final Object value) {
    this.address = address;
    this.type = type;
    this.value = value;
  }

  public int getAddress() {
    return this.address;
  }

  public Iec60870InformationObjectType getType() {
    return this.type;
  }

  public Object getValue() {
    return this.value;
  }
}
