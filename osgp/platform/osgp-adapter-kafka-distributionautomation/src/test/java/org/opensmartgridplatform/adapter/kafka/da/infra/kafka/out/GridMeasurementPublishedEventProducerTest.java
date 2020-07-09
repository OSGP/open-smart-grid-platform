/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.infra.kafka.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.kafka.test.assertj.KafkaConditions.value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.kafka.da.application.config.KafkaProducerConfig;
import org.opensmartgridplatform.adapter.kafka.da.application.mapping.DistributionAutomationMapper;
import org.opensmartgridplatform.adapter.kafka.da.avro.GridMeasurementPublishedEvent;
import org.opensmartgridplatform.adapter.kafka.da.avro.Measurement;
import org.opensmartgridplatform.adapter.kafka.da.avro.Name;
import org.opensmartgridplatform.adapter.kafka.da.avro.PowerSystemResource;
import org.opensmartgridplatform.adapter.kafka.da.serialization.GridMeasurementPublishedEventDeserializer;
import org.opensmartgridplatform.domain.da.measurements.MeasurementReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(KafkaProducerConfig.class)
@TestPropertySource("classpath:osgp-adapter-kafka-distributionautomation-test.properties")
@ExtendWith(MockitoExtension.class)
@EmbeddedKafka(partitions = 1,
        topics = { "${distributionautomation.kafka.topic}" },
        brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "log.dirs=../kafka-logs/",
                "auto.create.topics.enable=true" })
class GridMeasurementPublishedEventProducerTest {

    @Value("${distributionautomation.kafka.topic}")
    private String topic;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Mock
    private DistributionAutomationMapper mapper;

    @Autowired
    private KafkaTemplate<String, GridMeasurementPublishedEvent> template;

    private GridMeasurementPublishedEventProducer producer;
    private GridMeasurementPublishedEvent message;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        this.message = this.createMessage();
        when(this.mapper.map(any(MeasurementReport.class), any(Class.class))).thenReturn(this.message);
        this.producer = new GridMeasurementPublishedEventProducer(this.template, this.mapper);
    }

    @Test
    void sendTest() {

        // send a message to the kafka bus
        this.producer.send(new MeasurementReport.Builder().build());

        // consume the message with embeddedKafka
        final Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", this.embeddedKafka);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        final ConsumerFactory<String, GridMeasurementPublishedEvent> consumerFactory = new DefaultKafkaConsumerFactory<>(
                consumerProps, new StringDeserializer(), new GridMeasurementPublishedEventDeserializer());
        final Consumer<String, GridMeasurementPublishedEvent> consumer = consumerFactory.createConsumer();
        this.embeddedKafka.consumeFromAnEmbeddedTopic(consumer, this.topic);
        final ConsumerRecord<String, GridMeasurementPublishedEvent> received = KafkaTestUtils.getSingleRecord(consumer,
                this.topic);

        // check the consumed message
        assertThat(received).has(value(this.message));
    }

    private GridMeasurementPublishedEvent createMessage() {
        final long createdDateTime = System.currentTimeMillis();
        final String description = "description";
        final String mRid = "mRid";
        final String kind = "GridMeasurementPublishedEvent";
        final List<Measurement> measurements = new ArrayList<>();
        final List<Name> names = new ArrayList<>();
        final PowerSystemResource powerSystemResource = new PowerSystemResource(description, mRid, names);
        return new GridMeasurementPublishedEvent(createdDateTime, description, mRid, kind, measurements, names,
                powerSystemResource);
    }
}
