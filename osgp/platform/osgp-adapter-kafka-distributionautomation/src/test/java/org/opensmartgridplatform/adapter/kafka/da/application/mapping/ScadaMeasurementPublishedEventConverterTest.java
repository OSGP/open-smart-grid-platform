/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.alliander.data.scadameasurementpublishedevent.Analog;
import com.alliander.data.scadameasurementpublishedevent.ScadaMeasurementPublishedEvent;

class ScadaMeasurementPublishedEventConverterTest {

    private final DistributionAutomationMapper mapper = new DistributionAutomationMapper();

    @Test
    void testConvertScadaMeasurementPublishedEvent() {
        final String measurement = "[{\"gisnr\":\"TST-01-L-1V1\", \"feeder\":\"8\", \"D\": \"2020-08-29 09:00:00\", \"uts\":\"1598684400\", "
                + "\"data\": [0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,2.0,2.1,2.2,2.3,"
                + "2.4,2.5,2.6,2.7,2.8,2.9,3.0,3.1,3.2,3.3,3.4,3.5,3.6,3.7,3.8,3.9,4.0,4.1]}]";
        final ScadaMeasurementPublishedEvent event = this.mapper.map(measurement, ScadaMeasurementPublishedEvent.class);
        final List<Analog> measurements = event.getMeasurements();

        assertThat(event.getCreatedDateTime()).isEqualTo(1598684400l);
        assertThat(measurements).usingElementComparatorIgnoringFields("mRID")
                .isEqualTo(LsPeakShavingMessageFactory.expectedMeasurements());
    }

    @Test
    void testSomeOtherString() {
        final String someOtherString = "TST-01-L-1V1";
        final ScadaMeasurementPublishedEvent event = this.mapper.map(someOtherString,
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
