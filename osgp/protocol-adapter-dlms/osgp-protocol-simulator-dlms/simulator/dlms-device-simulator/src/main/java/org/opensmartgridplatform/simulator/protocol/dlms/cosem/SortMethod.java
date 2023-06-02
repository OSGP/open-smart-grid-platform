//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

public enum SortMethod {
  FIFO(1),
  LIFO(2),
  LARGEST(3),
  SMALLEST(4),
  NEAREST_TO_ZERO(5),
  FAREST_FROM_ZERO(6);

  int value;

  SortMethod(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }
}
