/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
