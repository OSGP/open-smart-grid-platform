//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.substation;

public enum LogicalNodeType {
  MMXU1("MMXU1"),
  MMXU2("MMXU2");

  private final String id;

  LogicalNodeType(final String id) {
    this.id = id;
  }

  public String getId() {
    return this.id;
  }
}
