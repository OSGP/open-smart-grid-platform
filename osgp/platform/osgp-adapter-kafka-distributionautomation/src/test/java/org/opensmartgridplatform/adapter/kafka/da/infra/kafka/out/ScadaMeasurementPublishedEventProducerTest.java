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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
import org.opensmartgridplatform.adapter.kafka.da.application.config.ScadaMeasurementKafkaProducerConfig;
import org.opensmartgridplatform.adapter.kafka.da.application.mapping.DistributionAutomationMapper;
import org.opensmartgridplatform.adapter.kafka.da.serialization.MessageDeserializer;
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

import com.alliander.data.scadameasurementpublishedevent.Analog;
import com.alliander.data.scadameasurementpublishedevent.BaseVoltage;
import com.alliander.data.scadameasurementpublishedevent.ConductingEquipment;
import com.alliander.data.scadameasurementpublishedevent.Message;
import com.alliander.data.scadameasurementpublishedevent.Name;
import com.alliander.data.scadameasurementpublishedevent.ScadaMeasurementPublishedEvent;

@SpringJUnitConfig(ScadaMeasurementKafkaProducerConfig.class)
@TestPropertySource("classpath:osgp-adapter-kafka-distributionautomation-test.properties")
@ExtendWith(MockitoExtension.class)
@EmbeddedKafka(partitions = 1,
        topics = { "${distributionautomation.kafka.topic}" },
        brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "log.dirs=../kafka-logs/",
                "auto.create.topics.enable=true" })
class ScadaMeasurementPublishedEventProducerTest {

    @Value("${distributionautomation.kafka.topic}")
    private String topic;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Mock
    private DistributionAutomationMapper mapper;

    @Autowired
    private KafkaTemplate<String, Message> template;

    private ScadaMeasurementPublishedEventProducer producer;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setup() {
        when(this.mapper.map(anyString(), any(Class.class))).thenReturn(this.createEvent());
        this.producer = new ScadaMeasurementPublishedEventProducer(this.template, this.mapper);
    }

    @Test
    void sendTest() {

        // send a message to the kafka bus
        this.producer.send("since we mock DistributionAutomationMapper, it doesn't matter what this string is");

        // consume the message with embeddedKafka
        final Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", this.embeddedKafka);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        final ConsumerFactory<String, Message> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps,
                new StringDeserializer(), new MessageDeserializer());
        final Consumer<String, Message> consumer = consumerFactory.createConsumer();
        this.embeddedKafka.consumeFromAnEmbeddedTopic(consumer, this.topic);
        final ConsumerRecord<String, Message> received = KafkaTestUtils.getSingleRecord(consumer, this.topic);

        // check the consumed message
        final Message actualMessage = received.value();
        assertThat(actualMessage.getMessageId()).isNotNull();
        assertThat(actualMessage.getProducerId().toString()).isEqualTo("GXF");
    }

    private ScadaMeasurementPublishedEvent createEvent() {
        final long createdDateTime = System.currentTimeMillis();
        final String description = "description";
        final String mRid = "mRid";
        final List<Analog> measurements = new ArrayList<>();
        final ConductingEquipment powerSystemResource = new ConductingEquipment(new BaseVoltage(description, null),
                new ArrayList<Name>());
        final ScadaMeasurementPublishedEvent event = new ScadaMeasurementPublishedEvent(measurements,
                powerSystemResource, createdDateTime, description, mRid);
        return event;
    }
}
