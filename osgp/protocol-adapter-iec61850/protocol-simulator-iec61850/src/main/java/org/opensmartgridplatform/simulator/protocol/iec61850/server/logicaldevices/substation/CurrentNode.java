// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.substation;

import java.security.SecureRandom;
import java.util.Random;

public class CurrentNode extends Node {
  private static final Random rand = new SecureRandom();

  public CurrentNode(
      final LogicalNodeNode logicalNodeNode, final String nodeId, final double value) {
    super(logicalNodeNode, nodeId, generateRandomCurrentValue(value));
  }

  private static double generateRandomCurrentValue(final double baseValue) {
    final double minValue = 0.5f * baseValue;
    final double maxValue = 1.5f * baseValue;
    return rand.nextDouble() * (maxValue - minValue) + minValue;
  }
}
