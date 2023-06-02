//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CosemObisCodeDto implements Serializable {

  private static final long serialVersionUID = -4096303352316355492L;

  private final int a;
  private final int b;
  private final int c;
  private final int d;
  private final int e;
  private final int f;

  public CosemObisCodeDto(
      final int a, final int b, final int c, final int d, final int e, final int f) {
    this.checkValues(a, b, c, d, e, f);
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
    this.e = e;
    this.f = f;
  }

  public CosemObisCodeDto(final int[] code) {
    if (code.length != 6) {
      throw new IllegalArgumentException("ObisCode must have 6 values: " + code.length);
    }
    this.a = code[0];
    this.b = code[1];
    this.c = code[2];
    this.d = code[3];
    this.e = code[4];
    this.f = code[5];
    this.checkValues(this.a, this.b, this.c, this.d, this.e, this.f);
  }

  public CosemObisCodeDto(final byte[] code) {
    if (code.length == 6) {
      this.a = code[0] & 0xFF;
      this.b = code[1] & 0xFF;
      this.c = code[2] & 0xFF;
      this.d = code[3] & 0xFF;
      this.e = code[4] & 0xFF;
      this.f = code[5] & 0xFF;
    } else {
      final int[] values = parseCode(new String(code, StandardCharsets.UTF_8));
      this.a = values[0];
      this.b = values[1];
      this.c = values[2];
      this.d = values[3];
      this.e = values[4];
      this.f = values[5];
    }
  }

  public CosemObisCodeDto(final String code) {
    this(parseCode(code));
  }

  private void checkValues(
      final int a, final int b, final int c, final int d, final int e, final int f) {
    this.checkValue("a", a);
    this.checkValue("b", b);
    this.checkValue("c", c);
    this.checkValue("d", d);
    this.checkValue("e", e);
    this.checkValue("f", f);
  }

  private void checkValue(final String letter, final int value) {
    if (value < 0 || value > 255) {
      throw new IllegalArgumentException(letter + " not in [0..0xFF]");
    }
  }

  private static int[] parseCode(final String code) {
    final String[] parts = code.split("\\.|:|-");
    if (parts.length != 6) {
      throw new IllegalArgumentException(
          "Unable to parse code into 6 integer parts: " + Arrays.toString(parts));
    }
    final int[] values = new int[6];
    for (int i = 0; i < 6; i++) {
      try {
        values[i] = Integer.parseInt(parts[i]);
      } catch (final NumberFormatException e) {
        throw new IllegalArgumentException(
            "Unable to parse code into 6 integer parts: " + Arrays.toString(parts), e);
      }
    }
    return values;
  }

  public int getA() {
    return this.a;
  }

  public int getB() {
    return this.b;
  }

  public int getC() {
    return this.c;
  }

  public int getD() {
    return this.d;
  }

  public int getE() {
    return this.e;
  }

  public int getF() {
    return this.f;
  }

  @Override
  public String toString() {
    return String.format("%d.%d.%d.%d.%d.%d", this.a, this.b, this.c, this.d, this.e, this.f);
  }

  public String toDsmrString() {
    return String.format("%d-%d:%d.%d.%d.%d", this.a, this.b, this.c, this.d, this.e, this.f);
  }

  public String toHexString() {
    return String.format(
        "%02X%02X%02X%02X%02X%02X", this.a, this.b, this.c, this.d, this.e, this.f);
  }

  public byte[] toByteArray() {
    return new byte[] {
      (byte) this.a, (byte) this.b, (byte) this.c, (byte) this.d, (byte) this.e, (byte) this.f
    };
  }

  public int[] toIntArray() {
    return new int[] {this.a, this.b, this.c, this.d, this.e, this.f};
  }
}
