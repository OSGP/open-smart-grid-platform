//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.substation;

public class LogicalNodeNode {
  private final LogicalDeviceNode logicalDeviceNode;
  private final LogicalNodeType logicalNodeType;

  public LogicalNodeNode(
      final LogicalDeviceNode logicalDeviceNode, final LogicalNodeType logicalNodeType) {
    this.logicalDeviceNode = logicalDeviceNode;
    this.logicalNodeType = logicalNodeType;
  }

  public LogicalDeviceNode getLogicalDeviceNode() {
    return this.logicalDeviceNode;
  }

  public LogicalNodeType getLogicalNodeType() {
    return this.logicalNodeType;
  }
}
