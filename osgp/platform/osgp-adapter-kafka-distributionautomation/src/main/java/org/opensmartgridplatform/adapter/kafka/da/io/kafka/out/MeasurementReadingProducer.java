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
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MeasurementReadingProducer implements InitializingBean, DisposableBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementReadingProducer.class);

    @Value("${distributionautomation.kafka.bootstrap.servers:localhost:9092,localhost:9093}")
    private String bootstrapServers;

    @Value("${distributionautomation.kafka.key.serializer:org.apache.kafka.common.serialization.LongSerializer}")
    private String keySerializer;

    @Value("${distributionautomation.kafka.value.serializer:io.confluent.kafka.serializers.KafkaAvroSerializer}")
    private String valueSerializer;

    @Value("${distributionautomation.kafka.schema.registry.url:http://localhost:8081}")
    private String schemaRegistryUrl;

    @Value("${distributionautomation.kafka.topic.meterreading.transformer.power:hackathon}")
    private String topicTransformerPower;

    @Value("${distributionautomation.kafka.topic.meterreading.transformer.internaltemperature:hackathon}")
    private String topicTransformerInternalTemperature;

    private Producer<String, MeterReading> producer;

    @Autowired
    protected DistributionAutomationMapper mapper;

    public void send(final MeasurementReport report) {
        try {
            LOGGER.info("Sending report: {}", report);
            final MeterReading meterReading = this.mapper.map(report, MeterReading.class);
            final String topic = this.getTopic(report);
            final ProducerRecord<String, MeterReading> producerRecord = new ProducerRecord<>(topic, null, meterReading);
            this.producer.send(producerRecord, new EventCallback(meterReading));
        } catch (final Exception ex) {
            // only log the exception, let the rest of the application run
            LOGGER.error("Exception when sending info", ex);
        }
    }

    private String getTopic(final MeasurementReport report) {
        String topic;
        if (this.isPowerMeterReading(report)) {
            topic = this.topicTransformerPower;
        } else {
            topic = this.topicTransformerInternalTemperature;
        }
        return topic;
    }

    private boolean isPowerMeterReading(final MeasurementReport report) {
        return report.getMeasurementGroups()
                .get(0)
                .getIdentification()
                .contains("Power");
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

    @Override
    public void afterPropertiesSet() throws Exception {
        final Properties props = new Properties();
        props.put("bootstrap.servers", this.bootstrapServers);
        props.put("key.serializer", this.keySerializer);
        props.put("value.serializer", this.valueSerializer);
        props.put("schema.registry.url", this.schemaRegistryUrl);

        this.producer = new KafkaProducer<>(props);
    }

    @Override
    public void destroy() throws Exception {
        this.producer.close();
    }

}
