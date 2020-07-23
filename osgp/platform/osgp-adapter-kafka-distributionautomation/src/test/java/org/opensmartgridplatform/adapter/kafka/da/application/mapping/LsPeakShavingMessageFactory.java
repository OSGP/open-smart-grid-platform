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

public class LsPeakShavingMessageFactory {
    public static List<Analog> expectedMeasurements() {
        final List<Analog> measurements = new ArrayList<>();
        measurements.add(new Analog("TST-01-L-1V1:U-L1-E", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.V, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(0.1f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:U-L2-E", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.V, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(0.2f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:U-L3-E", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.V, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(0.3f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I-L1", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(0.4f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I-L2", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(0.5f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I-L3", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(0.6f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:SomP", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.k, UnitSymbol.W, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(0.7f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:SomQ", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.k, UnitSymbol.VAr, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(0.8f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:P-L1", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.k, UnitSymbol.W, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(0.9f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:P-L2", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.k, UnitSymbol.W, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(1.0f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:P-L3", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.k, UnitSymbol.W, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(1.1f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:Q-L1", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.k, UnitSymbol.VAr, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(1.2f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:Q-L2", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.k, UnitSymbol.VAr, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(1.3f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:Q-L3", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.k, UnitSymbol.VAr, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(1.4f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:PF-L1", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.none, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(1.5f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:PF-L2", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.none, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(1.6f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:PF-L3", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.none, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(1.7f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:THDi-L1", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.PerCent, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(1.8f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:THDi-L2", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.PerCent, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(1.9f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:THDi-L3", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.PerCent, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(2.0f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I1-H3", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(2.1f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I2-H3", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(2.2f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I3-H3", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(2.3f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I1-H5", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(2.4f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I2-H5", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(2.5f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I3-H5", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(2.6f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I1-H7", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(2.7f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I2-H7", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(2.8f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I3-H7", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(2.9f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I1-H9", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(3.0f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I2-H9", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(3.1f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I3-H9", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(3.2f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I1-H11", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(3.3f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I2-H11", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(3.4f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I3-H11", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(3.5f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I1-H13", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(3.6f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I2-H13", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(3.7f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I3-H13", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(3.8f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I1-H15", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(3.9f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I2-H15", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(4.0f, null, null))));
        measurements.add(new Analog("TST-01-L-1V1:I3-H15", null, AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(4.1f, null, null))));
        return measurements;

    }

}
