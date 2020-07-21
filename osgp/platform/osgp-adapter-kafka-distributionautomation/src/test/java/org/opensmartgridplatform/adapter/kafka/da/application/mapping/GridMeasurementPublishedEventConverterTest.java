/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.kafka.da.avro.AccumulationKind;
import org.opensmartgridplatform.adapter.kafka.da.avro.Analog;
import org.opensmartgridplatform.adapter.kafka.da.avro.AnalogValue;
import org.opensmartgridplatform.adapter.kafka.da.avro.GridMeasurementPublishedEvent;
import org.opensmartgridplatform.adapter.kafka.da.avro.MeasuringPeriodKind;
import org.opensmartgridplatform.adapter.kafka.da.avro.Name;
import org.opensmartgridplatform.adapter.kafka.da.avro.PhaseCode;
import org.opensmartgridplatform.adapter.kafka.da.avro.UnitMultiplier;
import org.opensmartgridplatform.adapter.kafka.da.avro.UnitSymbol;

class GridMeasurementPublishedEventConverterTest {

    private final DistributionAutomationMapper mapper = new DistributionAutomationMapper();

    @Test
    void testConvertMeasurement() {
        final String measurement = "TST-01; 220.1; 220.2; 220.3; 5.1; 5.2; 5.3; 7.1; 7.2; 7.3;";
        final GridMeasurementPublishedEvent event = this.mapper.map(measurement, GridMeasurementPublishedEvent.class);
        final List<Analog> measurements = event.getMeasurements();

        assertThat(measurements).usingElementComparatorIgnoringFields("mRID").isEqualTo(this.expectedMeasurements());
    }

    @Test
    void testSomeOtherString() {
        final String someOtherString = "TST-01";
        final GridMeasurementPublishedEvent event = this.mapper.map(someOtherString,
                GridMeasurementPublishedEvent.class);

        assertThat(event).isNull();

    }

    private List<Analog> expectedMeasurements() {
        final List<Analog> measurements = new ArrayList<>();
        measurements.addAll(this.expectedVoltageMeasurements());
        measurements.addAll(this.expectedCurrentMeasurements());
        return measurements;
    }

    private List<Analog> expectedVoltageMeasurements() {
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

    private List<Analog> expectedCurrentMeasurements() {
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
