// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.cosem;

public enum AdministrativeStatusType {
  UNDEFINED(0),
  OFF(1),
  ON(2);

  int value;

  AdministrativeStatusType(final int value) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }
}
