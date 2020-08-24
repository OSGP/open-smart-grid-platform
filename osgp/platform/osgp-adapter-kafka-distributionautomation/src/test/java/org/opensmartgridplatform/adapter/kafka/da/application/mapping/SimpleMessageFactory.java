/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import java.util.ArrayList;
import java.util.List;

import org.opensmartgridplatform.adapter.kafka.da.avro.Analog;
import org.opensmartgridplatform.adapter.kafka.da.avro.UnitSymbol;

public class SimpleMessageFactory extends MessageFactory {
    public static List<Analog> expectedMeasurements() {
        final List<Analog> measurements = new ArrayList<>();
        measurements.addAll(expectedVoltageMeasurements());
        measurements.addAll(expectedCurrentMeasurements());
        return measurements;
    }

    private static List<Analog> expectedVoltageMeasurements() {
        final List<Analog> measurements = new ArrayList<>();
        measurements.add(createAnalog("TST-01:voltage_L1", 220.1f, UnitSymbol.V));
        measurements.add(createAnalog("TST-01:voltage_L2", 220.2f, UnitSymbol.V));
        measurements.add(createAnalog("TST-01:voltage_L3", 220.3f, UnitSymbol.V));
        return measurements;
    }

    private static List<Analog> expectedCurrentMeasurements() {
        final List<Analog> measurements = new ArrayList<>();
        measurements.add(createAnalog("TST-01:current_in_L1", 5.1f, UnitSymbol.A));
        measurements.add(createAnalog("TST-01:current_in_L2", 5.2f, UnitSymbol.A));
        measurements.add(createAnalog("TST-01:current_in_L3", 5.3f, UnitSymbol.A));
        measurements.add(createAnalog("TST-01:current_returned_L1", 7.1f, UnitSymbol.A));
        measurements.add(createAnalog("TST-01:current_returned_L2", 7.2f, UnitSymbol.A));
        measurements.add(createAnalog("TST-01:current_returned_L3", 7.3f, UnitSymbol.A));
        return measurements;

    }

}
