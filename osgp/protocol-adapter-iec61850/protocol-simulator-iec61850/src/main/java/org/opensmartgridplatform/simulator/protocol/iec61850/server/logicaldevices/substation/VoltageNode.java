/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.substation;

import java.util.Random;

public class VoltageNode extends Node {
  public VoltageNode(
      final LogicalNodeNode logicalNodeNode, final String nodeId, final double value) {
    super(logicalNodeNode, nodeId, generateRandomVoltageValue(value));
  }

  private static double generateRandomVoltageValue(final double baseValue) {
    final double minValue = 0.9f * baseValue;
    final double maxValue = 1.1f * baseValue;
    final Random rand = new Random();
    return rand.nextDouble() * (maxValue - minValue) + minValue;
  }
}
