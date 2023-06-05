// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.substation;

public class TotalNode extends Node {
  private static final String MAGNITUDE_TOTAL_NODE = ".mag.f";

  public TotalNode(
      final LogicalNodeNode logicalNodeNode,
      final String nodeId,
      final Node node1,
      final Node node2,
      final Node node3) {
    super(logicalNodeNode, nodeId, node1.getValue() + node2.getValue() + node3.getValue());
  }

  @Override
  protected String getMagnitudeNodeName() {
    return this.getServerName()
        + "/"
        + this.getLogicalNodeId()
        + "."
        + this.getNodeId()
        + MAGNITUDE_TOTAL_NODE;
  }
}
