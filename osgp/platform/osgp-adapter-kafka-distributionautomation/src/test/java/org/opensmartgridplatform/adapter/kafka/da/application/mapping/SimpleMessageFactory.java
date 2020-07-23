/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opensmartgridplatform.adapter.kafka.da.avro.AccumulationKind;
import org.opensmartgridplatform.adapter.kafka.da.avro.Analog;
import org.opensmartgridplatform.adapter.kafka.da.avro.AnalogValue;
import org.opensmartgridplatform.adapter.kafka.da.avro.MeasuringPeriodKind;
import org.opensmartgridplatform.adapter.kafka.da.avro.Name;
import org.opensmartgridplatform.adapter.kafka.da.avro.PhaseCode;
import org.opensmartgridplatform.adapter.kafka.da.avro.UnitMultiplier;
import org.opensmartgridplatform.adapter.kafka.da.avro.UnitSymbol;

public class SimpleMessageFactory {
    public static List<Analog> expectedMeasurements() {
        final List<Analog> measurements = new ArrayList<>();
        measurements.addAll(expectedVoltageMeasurements());
        measurements.addAll(expectedCurrentMeasurements());
        return measurements;
    }

    private static List<Analog> expectedVoltageMeasurements() {
        final List<Analog> measurements = new ArrayList<>();
        measurements.add(new Analog("TST-01:voltage_L1", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.V, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(220.1f, null, null))));
        measurements.add(new Analog("TST-01:voltage_L2", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.V, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(220.2f, null, null))));
        measurements.add(new Analog("TST-01:voltage_L3", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.V, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(220.3f, null, null))));
        return measurements;
    }

    private static List<Analog> expectedCurrentMeasurements() {
        final List<Analog> measurements = new ArrayList<>();
        measurements.add(new Analog("TST-01:current_in_L1", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(5.1f, null, null))));
        measurements.add(new Analog("TST-01:current_in_L2", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(5.2f, null, null))));
        measurements.add(new Analog("TST-01:current_in_L3", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(5.3f, null, null))));
        measurements.add(new Analog("TST-01:current_returned_L1", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(7.1f, null, null))));
        measurements.add(new Analog("TST-01:current_returned_L2", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(7.2f, null, null))));
        measurements.add(new Analog("TST-01:current_returned_L3", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(7.3f, null, null))));
        return measurements;

    }

}
