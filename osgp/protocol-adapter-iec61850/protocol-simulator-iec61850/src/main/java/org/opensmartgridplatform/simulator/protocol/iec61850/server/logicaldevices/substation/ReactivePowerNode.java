/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
