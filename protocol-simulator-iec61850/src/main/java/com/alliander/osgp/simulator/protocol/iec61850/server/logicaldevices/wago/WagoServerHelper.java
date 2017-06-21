/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulator.protocol.iec61850.server.logicaldevices.wago;

import org.openmuc.openiec61850.BasicDataAttribute;
import org.openmuc.openiec61850.ServerModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WagoServerHelper {
    private WagoServerHelper() {
        // Only static utility methods
    }
    /*
        For a Wago Device we assume the Voltage for field 0 is 10kV en for field 1 it is 230V
        The current for L1 is 50A, for L2 it is 100A and for L3 it is 150A
        Power equals Voltage * Current * cosinus phi (we try to use cos phi is 0.95 +/- 0.02)
        Reactive Power equals Voltage * Current * sinus phi
     */
    public static List<WagoNode> initializeServerNodes(final ServerModel serverModel) {
        final List<WagoNode> wagoNodes = new ArrayList<>();
        for (final WagoField wagoField : WagoField.values()) {
            wagoNodes.addAll(initializeServerNodesForField(serverModel, wagoField));
        }
        return wagoNodes;
    }

    public static List<WagoNode> initializeServerNodesForField(final ServerModel serverModel, final WagoField wagoField) {
        final WagoServerField wagoServerField = new WagoServerField(serverModel, wagoField);
        final WagoCurrentNode al1 = new WagoCurrentNode(wagoServerField, "A.phsA", 50d);
        final WagoCurrentNode al2 = new WagoCurrentNode(wagoServerField, "A.phsB", 100d);
        final WagoCurrentNode al3 = new WagoCurrentNode(wagoServerField, "A.phsC", 150d);
        final WagoVoltageNode ppv1 = new WagoVoltageNode(wagoServerField, "PPV.phsAB", (WagoField.FIELD0 == wagoField) ? 10d : 0.230d);
        final WagoVoltageNode ppv2 = new WagoVoltageNode(wagoServerField, "PPV.phsBC", (WagoField.FIELD0 == wagoField) ? 10d : 0.230d);
        final WagoVoltageNode ppv3 = new WagoVoltageNode(wagoServerField, "PPV.phsCA", (WagoField.FIELD0 == wagoField) ? 10d : 0.230d);
        final double phi1 = generatePhi();
        final double phi2 = generatePhi();
        final double phi3 = generatePhi();
        final WagoNode w1 = new WagoPowerNode(wagoServerField, "W.phsA", al1, ppv1, phi1);
        final WagoNode w2 = new WagoPowerNode(wagoServerField, "W.phsB", al2, ppv2, phi2);
        final WagoNode w3 = new WagoPowerNode(wagoServerField, "W.phsC", al3, ppv3, phi3);
        final WagoNode var1 = new WagoReactivePowerNode(wagoServerField, "VAr.phsA", al1, ppv1, phi1);
        final WagoNode var2 = new WagoReactivePowerNode(wagoServerField, "VAr.phsB", al2, ppv2, phi2);
        final WagoNode var3 = new WagoReactivePowerNode(wagoServerField, "VAr.phsC", al3, ppv3, phi3);
        final WagoNode totW = new WagoTotalNode(wagoServerField, "TotW", w1, w2, w3);
        final WagoNode totVar = new WagoTotalNode(wagoServerField, "TotVAr", var1, var2, var3);
        final List<WagoNode> wagoNodes = new ArrayList<>();
        wagoNodes.add(al1);
        wagoNodes.add(al2);
        wagoNodes.add(al3);
        wagoNodes.add(ppv1);
        wagoNodes.add(ppv2);
        wagoNodes.add(ppv3);
        wagoNodes.add(w1);
        wagoNodes.add(w2);
        wagoNodes.add(w3);
        wagoNodes.add(var1);
        wagoNodes.add(var2);
        wagoNodes.add(var3);
        wagoNodes.add(totW);
        wagoNodes.add(totVar);
        return wagoNodes;
    }

    public static List<BasicDataAttribute> getAllChangedAttributes(final List<WagoNode> wagoNodes) {
        final List<BasicDataAttribute> changedAttributes = new ArrayList<>();
        for (final WagoNode wagoNode :wagoNodes) {
            changedAttributes.addAll(wagoNode.getChangedAttributes());
        }
        return changedAttributes;
    }



    private static double generatePhi() {
        /*
            We aim to randomize cosinus phi to 0.95 +/- 0.02
            Cosinus(0.24d) = 0.9713379748520297
            Cosinus(0.38d) = 0.9286646355765102
         */
        final double minValue = 0.24d;
        final double maxValue = 0.38d;
        final Random rand = new Random();
        return rand.nextDouble() * (maxValue - minValue) + minValue;
    }
}