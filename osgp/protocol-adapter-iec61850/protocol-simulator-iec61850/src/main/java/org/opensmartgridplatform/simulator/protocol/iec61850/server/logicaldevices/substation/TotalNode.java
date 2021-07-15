/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
