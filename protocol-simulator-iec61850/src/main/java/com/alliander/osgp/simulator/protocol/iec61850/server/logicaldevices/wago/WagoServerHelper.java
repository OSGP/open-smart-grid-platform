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
        final WagoCurrentNode AL1 = new WagoCurrentNode(wagoServerField, "A.phsA", 50d);
        final WagoCurrentNode AL2 = new WagoCurrentNode(wagoServerField, "A.phsB", 100d);
        final WagoCurrentNode AL3 = new WagoCurrentNode(wagoServerField, "A.phsC", 150d);
        final WagoVoltageNode PPV1 = new WagoVoltageNode(wagoServerField, "PPV.phsAB", ((WagoField.FIELD0 == wagoField) ? 10d : 0.230d));
        final WagoVoltageNode PPV2 = new WagoVoltageNode(wagoServerField, "PPV.phsBC", ((WagoField.FIELD0 == wagoField) ? 10d : 0.230d));
        final WagoVoltageNode PPV3 = new WagoVoltageNode(wagoServerField, "PPV.phsCA", ((WagoField.FIELD0 == wagoField) ? 10d : 0.230d));
        final double phi1 = generatePhi();
        final double phi2 = generatePhi();
        final double phi3 = generatePhi();
        final WagoNode W1 = new WagoPowerNode(wagoServerField, "W.phsA", AL1, PPV1, phi1);
        final WagoNode W2 = new WagoPowerNode(wagoServerField, "W.phsB", AL2, PPV2, phi2);
        final WagoNode W3 = new WagoPowerNode(wagoServerField, "W.phsC", AL3, PPV3, phi3);
        final WagoNode VAR1 = new WagoReactivePowerNode(wagoServerField, "VAr.phsA", AL1, PPV1, phi1);
        final WagoNode VAR2 = new WagoReactivePowerNode(wagoServerField, "VAr.phsB", AL2, PPV2, phi2);
        final WagoNode VAR3 = new WagoReactivePowerNode(wagoServerField, "VAr.phsC", AL3, PPV3, phi3);
        final WagoNode TOTW = new WagoTotalNode(wagoServerField, "TotW", W1, W2, W3);
        final WagoNode TOTVAR = new WagoTotalNode(wagoServerField, "TotVAr", VAR1, VAR2, VAR3);
        return new ArrayList<WagoNode>() {
            {
                add(AL1);
                add(AL2);
                add(AL3);
                add(PPV1);
                add(PPV2);
                add(PPV3);
                add(W1);
                add(W2);
                add(W3);
                add(VAR1);
                add(VAR2);
                add(VAR3);
                add(TOTW);
                add(TOTVAR);
            }
        };
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
        return (rand.nextDouble() * (maxValue - minValue) + minValue);
    }
}