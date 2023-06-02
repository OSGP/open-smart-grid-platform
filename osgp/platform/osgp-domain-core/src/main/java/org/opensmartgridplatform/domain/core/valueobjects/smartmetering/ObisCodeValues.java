//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Objects;

public class ObisCodeValues implements Serializable {

  private static final long serialVersionUID = 7600691210538237856L;

  private byte a;
  private byte b;
  private byte c;
  private byte d;
  private byte e;
  private byte f;

  public ObisCodeValues(
      final byte a, final byte b, final byte c, final byte d, final byte e, final byte f) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
    this.e = e;
    this.f = f;
  }

  public byte getA() {
    return this.a;
  }

  public byte getB() {
    return this.b;
  }

  public byte getC() {
    return this.c;
  }

  public byte getD() {
    return this.d;
  }

  public byte getE() {
    return this.e;
  }

  public byte getF() {
    return this.f;
  }

  @Override
  public String toString() {
    return String.format(
        "%d.%d.%d.%d.%d.%d",
        this.a & 0xFF, this.b & 0xFF, this.c & 0xFF, this.d & 0xFF, this.e & 0xFF, this.f & 0xFF);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.a, this.b, this.c, this.d, this.e, this.f);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ObisCodeValues)) {
      return false;
    }
    final ObisCodeValues other = (ObisCodeValues) obj;
    return this.a == other.a
        && this.b == other.b
        && this.c == other.c
        && this.d == other.d
        && this.e == other.e
        && this.f == other.f;
  }
}
