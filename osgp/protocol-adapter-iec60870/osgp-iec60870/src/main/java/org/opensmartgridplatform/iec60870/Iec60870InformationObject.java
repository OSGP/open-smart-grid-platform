// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
