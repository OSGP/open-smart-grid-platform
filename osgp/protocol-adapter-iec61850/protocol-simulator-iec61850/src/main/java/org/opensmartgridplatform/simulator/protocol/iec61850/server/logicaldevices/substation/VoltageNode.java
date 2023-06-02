//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.substation;

import java.security.SecureRandom;
import java.util.Random;

public class VoltageNode extends Node {

  private static final Random rand = new SecureRandom();

  public VoltageNode(
      final LogicalNodeNode logicalNodeNode, final String nodeId, final double value) {
    super(logicalNodeNode, nodeId, generateRandomVoltageValue(value));
  }

  private static double generateRandomVoltageValue(final double baseValue) {
    final double minValue = 0.9f * baseValue;
    final double maxValue = 1.1f * baseValue;
    return rand.nextDouble() * (maxValue - minValue) + minValue;
  }
}
