//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.substation;

public class PowerNode extends Node {
  public PowerNode(
      final LogicalNodeNode logicalNodeNode,
      final String nodeId,
      final CurrentNode currentNode,
      final VoltageNode voltageNode,
      final double phi) {
    super(logicalNodeNode, nodeId, generatePowerValue(currentNode, voltageNode, phi));
  }

  private static double generatePowerValue(
      final CurrentNode currentNode, final VoltageNode voltageNode, final double phi) {
    return currentNode.getValue() * voltageNode.getValue() * Math.cos(phi);
  }
}
