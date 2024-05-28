// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.enums;

public enum SortMethod {
  FIFO(1),
  LIFO(2),
  LARGEST(3),
  SMALLEST(4),
  NEAREST_TO_ZERO(5),
  FURTHEST_FROM_ZERO(6),
  UNKNOWN_SORT_METHOD(255);

  private final int value;

  SortMethod(final int value) {
    this.value = value;
  }

  public static SortMethod getByValue(final int value) {
    for (final SortMethod method : SortMethod.values()) {
      if (method.value == value) {
        return method;
      }
    }

    return UNKNOWN_SORT_METHOD;
  }
}
