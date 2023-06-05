// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
    this.producer.send(PAYLOAD);
    this.assertMessageCanBeConsumedWithExpectedPayload(PAYLOAD);
  }

  private void assertMessageCanBeConsumedWithExpectedPayload(final String payload) {
    final String actualMessage = this.retrieveMessageFromKafka();
    assertThat(actualMessage).isEqualTo(payload);
  }

  private String retrieveMessageFromKafka() {
    final Consumer<String, String> consumer = this.createConsumer();
    this.embeddedKafka.consumeFromAnEmbeddedTopic(consumer, this.topic);
    final ConsumerRecord<String, String> received =
        KafkaTestUtils.getSingleRecord(consumer, this.topic);
    return received.value();
  }

  private Consumer<String, String> createConsumer() {
    final Map<String, Object> consumerProps =
        KafkaTestUtils.consumerProps("testGroup", "true", this.embeddedKafka);
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    final ConsumerFactory<String, String> consumerFactory =
        new DefaultKafkaConsumerFactory<>(
            consumerProps, new StringDeserializer(), new StringDeserializer());
    return consumerFactory.createConsumer();
  }

  @AfterEach
  public void destroy() {
    this.embeddedKafka.destroy();
  }
}
