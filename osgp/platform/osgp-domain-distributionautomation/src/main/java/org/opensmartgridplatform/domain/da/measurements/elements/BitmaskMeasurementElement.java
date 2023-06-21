// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.da.measurements.elements;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import org.opensmartgridplatform.domain.da.measurements.MeasurementElement;

public class BitmaskMeasurementElement implements MeasurementElement {

  private static final long serialVersionUID = 1L;

  private final Byte value;

  public BitmaskMeasurementElement(final Byte value) {
    this.value = value;
  }

  public Byte getValue() {
    return this.value;
  }

  public boolean getFlag(final BitmaskFlag flag) {
    return (this.value & flag.value) > 0;
  }

  public Set<BitmaskFlag> asEnumSet() {
    final Set<BitmaskFlag> bitmaskFlags = EnumSet.noneOf(BitmaskFlag.class);
    for (final BitmaskFlag flag : BitmaskFlag.values()) {
      if ((this.value & flag.value) > 0) {
        bitmaskFlags.add(flag);
      }
    }
    return bitmaskFlags;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof BitmaskMeasurementElement)) {
      return false;
    }
    final BitmaskMeasurementElement that = (BitmaskMeasurementElement) obj;
    return this.value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.value);
  }

  @Override
  public String toString() {
    return "Bitmask: " + this.value;
  }

  public enum BitmaskFlag {
    FLAG_1(1 << 0), // 1
    FLAG_2(1 << 1), // 2
    FLAG_3(1 << 2), // 4
    FLAG_4(1 << 3), // 8
    FLAG_5(1 << 4), // 16
    FLAG_6(1 << 5), // 32
    FLAG_7(1 << 6), // 64
    FLAG_8(1 << 7); // 128

    private final int value;

    private BitmaskFlag(final int value) {
      this.value = value;
    }

    public int getValue() {
      return this.value;
    }
  }
}
