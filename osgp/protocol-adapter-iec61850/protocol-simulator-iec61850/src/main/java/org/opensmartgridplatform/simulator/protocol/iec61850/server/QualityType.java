// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec61850.server;

public enum QualityType {
  VALIDITY_GOOD((short) 0b0000000000000),
  VALIDITY_INVALID((short) 0b0100000000000),
  VALIDITY_RESERVED((short) 0b1000000000000),
  VALIDITY_QUESTIONABLE((short) 0b1100000000000),
  OVERFLOW((short) 0b0010000000000),
  OUT_OF_RANGE((short) 0b0001000000000),
  BAD_REFERENCE((short) 0b0000100000000),
  OSCILLATORY((short) 0b0000010000000),
  FAILURE((short) 0b0000001000000),
  OLD_DATA((short) 0b0000000100000),
  INCONSISTANT((short) 0b0000000010000),
  INACCURATE((short) 0b0000000001000),
  SUBSTITUTED((short) 0b0000000000100),
  TEST((short) 0b0000000000010),
  OPERATOR_BLOCKED((short) 0b0000000000001);

  private short value;

  QualityType(final short value) {
    this.value = value;
  }

  public short getValue() {
    return this.value;
  }
}
