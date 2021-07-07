/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.infra.kafka.out;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.alliander.data.scadameasurementpublishedevent.Analog;
import com.alliander.data.scadameasurementpublishedevent.BaseVoltage;
import com.alliander.data.scadameasurementpublishedevent.ConductingEquipment;
import com.alliander.data.scadameasurementpublishedevent.Message;
import com.alliander.data.scadameasurementpublishedevent.ScadaMeasurementPublishedEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.kafka.da.application.config.LowVoltageMessageProducerConfig;
import org.opensmartgridplatform.adapter.kafka.da.application.mapping.DistributionAutomationMapper;
import org.opensmartgridplatform.adapter.kafka.da.application.services.LocationService;
import org.opensmartgridplatform.adapter.kafka.da.infra.mqtt.in.ScadaMeasurementPayload;
import org.opensmartgridplatform.adapter.kafka.da.serialization.MessageDeserializer;
import org.opensmartgridplatform.adapter.kafka.da.signature.MessageSigner;
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

@SpringJUnitConfig({LowVoltageMessageProducerConfig.class})
@TestPropertySource("classpath:osgp-adapter-kafka-distributionautomation-test.properties")
@ExtendWith(MockitoExtension.class)
@EmbeddedKafka(
    partitions = 1,
    topics = {"${distributionautomation.kafka.topic.low.voltage}"},
    brokerProperties = {
      "listeners=PLAINTEXT://localhost:9092",
      "log.dirs=../kafka-logs/",
      "auto.create.topics.enable=true"
    })
class LowVoltageMessageProducerTest {

  @Value("${distributionautomation.kafka.topic.low.voltage}")
  private String topic;

  @Autowired private EmbeddedKafkaBroker embeddedKafka;

  @Mock private LocationService locationService;

  @Mock private DistributionAutomationMapper mapper;

  @Autowired private KafkaTemplate<String, Message> distributionAutomationLowVoltageKafkaTemplate;

  @Autowired private MessageSigner messageSigner;

  private VoltageMessageProducer producer;

  private static final String PAYLOAD =
      "[{\"gisnr\":\"TST-01-L-1V1\", \"feeder\":\"8\", \"D\": \"02/10/2020 16:03:38\", "
          + "\"uts\":\"1601647418\", \"data\": [0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0,1.1,1.2,1.3,1.4,1.5,1.6,1.7,"
          + "1.8,1.9,2.0,2.1,2.2,2.3,2.4,2.5,2.6,2.7,2.8,2.9,3.0,3.1,3.2,3.3,3.4,3.5,3.6,3.7,3.8,3.9,4.0,4.1]}]";

  @BeforeEach
  @SuppressWarnings("unchecked")
  public void setup() {
    when(this.mapper.map(any(ScadaMeasurementPayload.class), any(Class.class)))
        .thenReturn(this.createEvent());
    this.producer =
        new VoltageMessageProducer(
            this.distributionAutomationLowVoltageKafkaTemplate,
            null,
            this.messageSigner,
            this.mapper,
            this.locationService);
  }

  @Test
  void sendTest() {

    // send a message to the kafka bus
    this.producer.send(PAYLOAD);

    // consume the message with embeddedKafka
    final Map<String, Object> consumerProps =
        KafkaTestUtils.consumerProps("testGroup", "true", this.embeddedKafka);
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    final ConsumerFactory<String, Message> consumerFactory =
        new DefaultKafkaConsumerFactory<>(
            consumerProps, new StringDeserializer(), new MessageDeserializer());
    final Consumer<String, Message> consumer = consumerFactory.createConsumer();
    this.embeddedKafka.consumeFromAnEmbeddedTopic(consumer, this.topic);
    final ConsumerRecord<String, Message> received =
        KafkaTestUtils.getSingleRecord(consumer, this.topic);

    // check the consumed message
    final Message actualMessage = received.value();
    assertThat(actualMessage.getMessageId()).isNotNull();
    assertThat(actualMessage.getProducerId()).hasToString("GXF");
    assertThat(this.messageSigner.verify(actualMessage)).isTrue();
  }

  private ScadaMeasurementPublishedEvent createEvent() {
    final long createdDateTime = System.currentTimeMillis();
    final String description = "description";
    final String mRid = "mRid";
    final List<Analog> measurements = new ArrayList<>();
    final ConductingEquipment powerSystemResource =
        new ConductingEquipment(new BaseVoltage(description, null), new ArrayList<>());
    return new ScadaMeasurementPublishedEvent(
        measurements, powerSystemResource, createdDateTime, description, mRid);
  }

  @AfterEach
  public void destroy() {
    this.embeddedKafka.destroy();
  }
}
