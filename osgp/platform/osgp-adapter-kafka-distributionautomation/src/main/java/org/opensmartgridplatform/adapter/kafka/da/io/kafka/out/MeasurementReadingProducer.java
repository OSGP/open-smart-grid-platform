/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.io.kafka.out;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.opensmartgridplatform.adapter.kafka.MeterReading;
import org.opensmartgridplatform.adapter.kafka.da.application.mapping.DistributionAutomationMapper;
import org.opensmartgridplatform.domain.da.measurements.MeasurementReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeasurementReadingProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementReadingProducer.class);

    @Autowired
    protected DistributionAutomationMapper mapper;

    public void send(final MeasurementReport report) {
        final Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092,localhost:9093");
        props.put("key.serializer", "org.apache.kafka.common.serialization.LongSerializer");
        props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put("schema.registry.url", "http://localhost:8081");

        try (final Producer<String, MeterReading> producer = new KafkaProducer<>(props)) {

            final MeterReading meterReading = this.mapper.map(report, MeterReading.class);
            final ProducerRecord<String, MeterReading> producerRecord = new ProducerRecord<>("hackathon", null,
                    meterReading);
            producer.send(producerRecord, new EventCallback(meterReading));
        } catch (final Exception ex) {
            // only log the exception, let the rest of the application run
            LOGGER.error("Exception when sending info", ex);
        }
    }

    private class EventCallback implements Callback {

        private final MeterReading meterReading;

        public EventCallback(final MeterReading meterReading) {
            this.meterReading = meterReading;
        }

        @Override
        public void onCompletion(final RecordMetadata metadata, final Exception exception) {

            if (exception != null) {

                // for now we only log an exception
                LOGGER.error("Error while sending information meterReading {}, RecordMetadata {}",
                        this.meterReading.getName(), metadata, exception);
            }

        }

    }

}
