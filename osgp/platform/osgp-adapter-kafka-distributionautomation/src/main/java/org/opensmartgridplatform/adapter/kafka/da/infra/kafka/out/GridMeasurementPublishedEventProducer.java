/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.infra.kafka.out;

import org.opensmartgridplatform.adapter.kafka.da.application.mapping.DistributionAutomationMapper;
import org.opensmartgridplatform.adapter.kafka.da.avro.GridMeasurementPublishedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class GridMeasurementPublishedEventProducer {

    private final KafkaTemplate<String, GridMeasurementPublishedEvent> kafkaTemplate;

    private final DistributionAutomationMapper mapper;

    @Autowired
    public GridMeasurementPublishedEventProducer(
            @Qualifier("distributionAutomationKafkaTemplate") final KafkaTemplate<String, GridMeasurementPublishedEvent> kafkaTemplate,
            final DistributionAutomationMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    public void send(final String measurementReport) {

        final GridMeasurementPublishedEvent meterReading = this.mapper.map(measurementReport,
                GridMeasurementPublishedEvent.class);
        /*
         * No need for callback functionality now; by default, the template is
         * configured with a LoggingProducerListener, which logs errors and does
         * nothing when the send is successful.
         */
        this.kafkaTemplate.sendDefault(meterReading);
    }

}
