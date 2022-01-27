/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.infra.kafka.out;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.kafka.da.application.config.StringMessageProducerConfig;
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

@SpringJUnitConfig({StringMessageProducerConfig.class})
@TestPropertySource("classpath:osgp-adapter-kafka-distributionautomation-test.properties")
@ExtendWith(MockitoExtension.class)
@EmbeddedKafka(
    partitions = 1,
    topics = {"${distributionautomation.kafka.topic.message}"},
    brokerProperties = {
      "listeners=PLAINTEXT://localhost:9092",
      "log.dirs=target/kafka-logs/",
      "auto.create.topics.enable=true"
    })
class StringMessageProducerTest {

  private static final String PAYLOAD = "string-message-payload";

  @Value("${distributionautomation.kafka.topic.message}")
  private String topic;

  @Autowired private EmbeddedKafkaBroker embeddedKafka;

  @Autowired private KafkaTemplate<String, String> kafkaTemplate;

  private StringMessageProducer producer;

  @BeforeEach
  public void setup() {
    this.producer = new StringMessageProducer(this.kafkaTemplate);
  }

  @Test
  void sendTest() {

    // send a message to the kafka bus
    this.producer.send(PAYLOAD);

    // consume the message with embeddedKafka
    final Map<String, Object> consumerProps =
        KafkaTestUtils.consumerProps("testGroup", "true", this.embeddedKafka);
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    final ConsumerFactory<String, String> consumerFactory =
        new DefaultKafkaConsumerFactory<>(
            consumerProps, new StringDeserializer(), new StringDeserializer());
    final Consumer<String, String> consumer = consumerFactory.createConsumer();
    this.embeddedKafka.consumeFromAnEmbeddedTopic(consumer, this.topic);
    final ConsumerRecord<String, String> received =
        KafkaTestUtils.getSingleRecord(consumer, this.topic);

    // check the consumed message
    final String actualMessage = received.value();
    assertThat(actualMessage).isEqualTo(PAYLOAD);
  }

  @AfterEach
  public void destroy() {
    this.embeddedKafka.destroy();
  }
}
