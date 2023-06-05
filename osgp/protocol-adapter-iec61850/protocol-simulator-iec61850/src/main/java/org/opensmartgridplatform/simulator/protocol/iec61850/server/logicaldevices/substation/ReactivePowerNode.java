// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.substation;

public class ReactivePowerNode extends Node {
  public ReactivePowerNode(
      final LogicalNodeNode logicalNodeNode,
      final String nodeId,
      final CurrentNode currentNode,
      final VoltageNode voltageNode,
      final double phi) {
    super(logicalNodeNode, nodeId, generateReactivePowerValue(currentNode, voltageNode, phi));
  }

  private static double generateReactivePowerValue(
      final CurrentNode currentNode, final VoltageNode voltageNode, final double phi) {
    return currentNode.getValue() * voltageNode.getValue() * Math.sin(phi);
  }
}
