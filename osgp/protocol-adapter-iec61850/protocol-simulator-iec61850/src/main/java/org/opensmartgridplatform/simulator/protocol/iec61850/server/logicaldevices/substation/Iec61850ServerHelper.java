//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.iec61850.server.logicaldevices.substation;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.ServerModel;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Iec61850ServerHelper {
  private static final Random rand = new SecureRandom();

  private Iec61850ServerHelper() {
    // Only static utility methods
  }
  /*
     For a Substation Device we assume the Voltage for field 0 is 10kV en for field 1 it is 230V
     The current for L1 is 50A, for L2 it is 100A and for L3 it is 150A
     Power equals Voltage * Current * cosinus phi (we try to use cos phi is 0.95 +/- 0.02)
     Reactive Power equals Voltage * Current * sinus phi
  */
  public static List<Node> initializeServerNodes(final ServerModel serverModel) {
    final LogicalDeviceNode logicalDeviceNode = new LogicalDeviceNode(serverModel);
    final List<Node> nodes = new ArrayList<>();
    for (final LogicalNodeType logicalNodeType : LogicalNodeType.values()) {
      nodes.addAll(initializeServerNodesForField(logicalDeviceNode, logicalNodeType));
    }
    return nodes;
  }

  public static List<BasicDataAttribute> getAllChangedAttributes(final List<Node> nodes) {
    final List<BasicDataAttribute> changedAttributes = new ArrayList<>();
    for (final Node node : nodes) {
      changedAttributes.addAll(node.getChangedAttributes());
    }
    return changedAttributes;
  }

  private static List<Node> initializeServerNodesForField(
      final LogicalDeviceNode logicalDeviceNode, final LogicalNodeType logicalNodeType) {
    final LogicalNodeNode logicalNodeNode = new LogicalNodeNode(logicalDeviceNode, logicalNodeType);
    final CurrentNode al1 = new CurrentNode(logicalNodeNode, "A.phsA", 50d);
    final CurrentNode al2 = new CurrentNode(logicalNodeNode, "A.phsB", 100d);
    final CurrentNode al3 = new CurrentNode(logicalNodeNode, "A.phsC", 150d);
    final VoltageNode ppv1 =
        new VoltageNode(
            logicalNodeNode,
            "PPV.phsAB",
            (LogicalNodeType.MMXU1 == logicalNodeType) ? 10d : 0.230d);
    final VoltageNode ppv2 =
        new VoltageNode(
            logicalNodeNode,
            "PPV.phsBC",
            (LogicalNodeType.MMXU1 == logicalNodeType) ? 10d : 0.230d);
    final VoltageNode ppv3 =
        new VoltageNode(
            logicalNodeNode,
            "PPV.phsCA",
            (LogicalNodeType.MMXU1 == logicalNodeType) ? 10d : 0.230d);
    final double phi1 = generatePhi();
    final double phi2 = generatePhi();
    final double phi3 = generatePhi();
    final Node w1 = new PowerNode(logicalNodeNode, "W.phsA", al1, ppv1, phi1);
    final Node w2 = new PowerNode(logicalNodeNode, "W.phsB", al2, ppv2, phi2);
    final Node w3 = new PowerNode(logicalNodeNode, "W.phsC", al3, ppv3, phi3);
    final Node var1 = new ReactivePowerNode(logicalNodeNode, "VAr.phsA", al1, ppv1, phi1);
    final Node var2 = new ReactivePowerNode(logicalNodeNode, "VAr.phsB", al2, ppv2, phi2);
    final Node var3 = new ReactivePowerNode(logicalNodeNode, "VAr.phsC", al3, ppv3, phi3);
    final Node totW = new TotalNode(logicalNodeNode, "TotW", w1, w2, w3);
    final Node totVar = new TotalNode(logicalNodeNode, "TotVAr", var1, var2, var3);
    final List<Node> nodes = new ArrayList<>();
    nodes.add(al1);
    nodes.add(al2);
    nodes.add(al3);
    nodes.add(ppv1);
    nodes.add(ppv2);
    nodes.add(ppv3);
    nodes.add(w1);
    nodes.add(w2);
    nodes.add(w3);
    nodes.add(var1);
    nodes.add(var2);
    nodes.add(var3);
    nodes.add(totW);
    nodes.add(totVar);
    return nodes;
  }

  private static double generatePhi() {
    /*
       We aim to randomize cosinus phi to 0.95 +/- 0.02
       Cosinus(0.24d) = 0.9713379748520297
       Cosinus(0.38d) = 0.9286646355765102
    */
    final double minValue = 0.24d;
    final double maxValue = 0.38d;
    return rand.nextDouble() * (maxValue - minValue) + minValue;
  }
}
