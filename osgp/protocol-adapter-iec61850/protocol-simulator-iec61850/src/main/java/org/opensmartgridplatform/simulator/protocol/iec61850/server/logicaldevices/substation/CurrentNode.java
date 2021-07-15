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

public class CurrentNode extends Node {
  public CurrentNode(
      final LogicalNodeNode logicalNodeNode, final String nodeId, final double value) {
    super(logicalNodeNode, nodeId, generateRandomCurrentValue(value));
  }

  private static double generateRandomCurrentValue(final double baseValue) {
    final double minValue = 0.5f * baseValue;
    final double maxValue = 1.5f * baseValue;
    final Random rand = new Random();
    return rand.nextDouble() * (maxValue - minValue) + minValue;
  }
}
