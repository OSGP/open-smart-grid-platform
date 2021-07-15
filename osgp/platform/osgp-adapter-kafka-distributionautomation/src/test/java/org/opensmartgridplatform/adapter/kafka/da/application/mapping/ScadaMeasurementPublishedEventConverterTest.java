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
import java.util.List;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.kafka.da.infra.mqtt.in.ScadaMeasurementPayload;

import com.alliander.data.scadameasurementpublishedevent.Analog;
import com.alliander.data.scadameasurementpublishedevent.Name;
import com.alliander.data.scadameasurementpublishedevent.NameType;
import com.alliander.data.scadameasurementpublishedevent.ScadaMeasurementPublishedEvent;

class ScadaMeasurementPublishedEventConverterTest {

    private final DistributionAutomationMapper mapper = new DistributionAutomationMapper();

    private static final String SUBSTATION_IDENTIFICATION = "TST-01-L-1V1";
    private static final String SUBSTATION_NAME = "Test location";
    private static final String BAY_IDENTIFICATION = "03FQ03";

    @Test
    void testConvertScadaMeasurementPublishedEvent() {
        final String data = "0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,2.0,2.1,2.2,2.3,"
                + "2.4,2.5,2.6,2.7,2.8,2.9,3.0,3.1,3.2,3.3,3.4,3.5,3.6,3.7,3.8,3.9,4.0,4.1";
        final int feeder = 8;
        final long utcSeconds = 1598684400;
        final ScadaMeasurementPayload payload = ScadaMeasurementPayload.builder()
                .substationIdentification(SUBSTATION_IDENTIFICATION)
                .substationName(SUBSTATION_NAME)
                .feeder(String.valueOf(feeder))
                .bayIdentification(BAY_IDENTIFICATION)
                .createdUtcSeconds(utcSeconds)
                .data(data.split(","))
                .build();
        final ScadaMeasurementPublishedEvent event = this.mapper.map(payload, ScadaMeasurementPublishedEvent.class);
        final List<Analog> measurements = event.getMeasurements();

        assertThat(event.getCreatedDateTime()).isEqualTo(utcSeconds * 1000L);
        assertThat(measurements).usingElementComparatorIgnoringFields("mRID")
                .isEqualTo(LovVoltageMessageFactory.expectedMeasurements());

        final List<Name> names = event.getPowerSystemResource().getNames();
        assertThat(names).containsExactlyElementsOf(this.expectedNames(feeder));
    }

    private List<Name> expectedNames(final int feeder) {
        final ArrayList<Name> names = new ArrayList<>();
        names.add(new Name(new NameType("gisbehuizingnummer"), SUBSTATION_IDENTIFICATION));
        names.add(new Name(new NameType("msr naam"), SUBSTATION_NAME));
        names.add(new Name(new NameType("bay positie"), String.valueOf(feeder)));
        if (feeder != 100) {
            names.add(new Name(new NameType("bay identificatie"), BAY_IDENTIFICATION));
        }
        return names;
    }

    @Test
    void testConvertScadaMetaMeasurementPublishedEvent() {
        final String data = "49.98,12.0,0.11";
        final int feeder = 100;
        final long utcSeconds = 1598684400;
        final ScadaMeasurementPayload payload = ScadaMeasurementPayload.builder()
                .substationIdentification(SUBSTATION_IDENTIFICATION)
                .substationName(SUBSTATION_NAME)
                .feeder(String.valueOf(feeder))
                .createdUtcSeconds(utcSeconds)
                .data(data.split(","))
                .build();
        final ScadaMeasurementPublishedEvent event = this.mapper.map(payload, ScadaMeasurementPublishedEvent.class);
        final List<Analog> measurements = event.getMeasurements();

        assertThat(event.getCreatedDateTime()).isEqualTo(utcSeconds * 1000l);
        assertThat(measurements).usingElementComparatorIgnoringFields("mRID")
                .isEqualTo(LovVoltageMessageFactory.expectedMetaMeasurements());

        final List<Name> names = event.getPowerSystemResource().getNames();
        assertThat(names).containsExactlyElementsOf(this.expectedNames(feeder));
    }

    @Test
    void testIncompletePayload() {
        final ScadaMeasurementPublishedEvent event = this.mapper.map(ScadaMeasurementPayload.builder().build(),
                ScadaMeasurementPublishedEvent.class);

        assertThat(event).isNull();

    }

    @Test
    void testNullString() {
        final String someNullString = null;
        final ScadaMeasurementPublishedEvent event = this.mapper.map(someNullString,
                ScadaMeasurementPublishedEvent.class);

        assertThat(event).isNull();
    }

}
