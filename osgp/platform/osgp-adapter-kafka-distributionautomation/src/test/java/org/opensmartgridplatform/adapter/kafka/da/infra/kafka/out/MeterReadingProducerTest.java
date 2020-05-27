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
import org.opensmartgridplatform.adapter.kafka.da.avro.IntervalBlock;
import org.opensmartgridplatform.adapter.kafka.da.avro.MeterReading;
import org.opensmartgridplatform.adapter.kafka.da.avro.UsagePoint;
import org.opensmartgridplatform.adapter.kafka.da.avro.ValuesInterval;
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
class MeterReadingProducerTest {

    @Value("${distributionautomation.kafka.topic}")
    private String topic;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Mock
    private DistributionAutomationMapper mapper;

    @Autowired
    private KafkaTemplate<String, MeterReading> template;

    private MeterReadingProducer producer;
    private MeterReading message;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        this.message = this.createMessage();
        when(this.mapper.map(any(MeasurementReport.class), any(Class.class))).thenReturn(this.message);
        this.producer = new MeterReadingProducer(this.template, this.mapper);
    }

    @Test
    void sendTest() {

        // send a message to the kafka bus
        this.producer.send(new MeasurementReport.Builder().build());

        // consume the message with embeddedKafka
        final Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", this.embeddedKafka);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        final ConsumerFactory<String, MeterReading> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps,
                new StringDeserializer(), new MeterReadingDeserializer());
        final Consumer<String, MeterReading> consumer = consumerFactory.createConsumer();
        this.embeddedKafka.consumeFromAnEmbeddedTopic(consumer, this.topic);
        final ConsumerRecord<String, MeterReading> received = KafkaTestUtils.getSingleRecord(consumer, this.topic);

        // check the consumed message
        assertThat(received).has(value(this.message));
    }

    private MeterReading createMessage() {
        final ValuesInterval valuesInterval = new ValuesInterval(0l, 1000l);
        final String mRid = "mRid";
        final UsagePoint usagePoint = new UsagePoint(mRid);
        final List<IntervalBlock> intervalBlocks = new ArrayList<>();
        return new MeterReading(valuesInterval, mRid, "name", usagePoint, intervalBlocks);
    }
}
