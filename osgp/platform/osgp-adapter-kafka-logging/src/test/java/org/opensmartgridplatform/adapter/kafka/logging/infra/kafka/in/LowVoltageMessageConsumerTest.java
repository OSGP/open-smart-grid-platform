/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.logging.infra.kafka.in;

import static org.assertj.core.api.Assertions.assertThat;

import com.alliander.data.scadameasurementpublishedevent.Analog;
import com.alliander.data.scadameasurementpublishedevent.BaseVoltage;
import com.alliander.data.scadameasurementpublishedevent.ConductingEquipment;
import com.alliander.data.scadameasurementpublishedevent.Message;
import com.alliander.data.scadameasurementpublishedevent.Name;
import com.alliander.data.scadameasurementpublishedevent.ScadaMeasurementPublishedEvent;
import com.alliander.messaging.MessageId;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.kafka.da.serialization.MessageSerializer;
import org.opensmartgridplatform.adapter.kafka.da.signature.MessageSigner;
import org.opensmartgridplatform.adapter.kafka.logging.config.ApplicationContext;
import org.opensmartgridplatform.adapter.kafka.logging.config.LowVoltageMessageLoggingEnabled;
import org.opensmartgridplatform.adapter.kafka.logging.infra.kafka.in.LowVoltageMessageConsumerTest.TestConfig;
import org.opensmartgridplatform.kafka.logging.CountDownKafkaLogger;
import org.opensmartgridplatform.kafka.logging.KafkaLogger;
import org.opensmartgridplatform.shared.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(TestConfig.class)
@TestPropertySource("classpath:osgp-adapter-kafka-logging-test.properties")
@EmbeddedKafka(
    partitions = 1,
    topics = {"${low.voltage.kafka.topic}"},
    brokerProperties = {
      "listeners=PLAINTEXT://${low.voltage.kafka.bootstrap.servers}",
      "log.dirs=target/kafka-logs-low-voltage-messages",
      "auto.create.topics.enable=true"
    })
@DirtiesContext
class LowVoltageMessageConsumerTest {

  /**
   * Spring test context configuration setting up the CountDownKafkaLogger to be used to verify
   * sending peak shaving data leads to it being logged.
   *
   * @see CountDownKafkaLogger
   */
  @Configuration
  @Import(ApplicationContext.class)
  @Conditional(LowVoltageMessageLoggingEnabled.class)
  public static class TestConfig {

    @Autowired MessageSigner messageSigner;

    @Bean
    public CountDownLatch countDownLatch() {
      return new CountDownLatch(1);
    }

    @Bean
    public KafkaLogger kafkaLogger(
        final CountDownLatch countDownLatch, final Message lowVoltageData) {
      return new CountDownKafkaLogger(countDownLatch, lowVoltageData);
    }

    @Bean
    public Message lowVoltageMessage() {
      final long createdDateTime = System.currentTimeMillis();
      final String description = "description";
      final String mRid = "mRid";
      final List<Analog> measurements = new ArrayList<>();
      final ConductingEquipment powerSystemResource =
          new ConductingEquipment(new BaseVoltage(description, null), new ArrayList<Name>());
      final ScadaMeasurementPublishedEvent event =
          new ScadaMeasurementPublishedEvent(
              measurements, powerSystemResource, createdDateTime, description, mRid);

      final MessageId messageId = new MessageId(UuidUtil.getBytesFromRandomUuid());
      final Message message =
          new Message(messageId, System.currentTimeMillis(), "GXF", null, event);
      this.messageSigner.sign(message);
      return message;
    }
  }

  @Value("${low.voltage.kafka.topic}")
  private String topic;

  @Autowired private CountDownLatch countDownLatch;

  @Autowired private Message lowVoltageMessage;

  @Autowired private EmbeddedKafkaBroker broker;

  @Test
  void logsMessagesPostedToTheTopic() throws Exception {
    this.whenLowVoltageDataIsSentToTheTopic();
    this.theKafkaLoggerLogsTheLowVoltageData();
  }

  private void whenLowVoltageDataIsSentToTheTopic() {
    final Map<String, Object> producerProps = KafkaTestUtils.producerProps(this.broker);
    final ProducerFactory<String, Message> producerFactory =
        new DefaultKafkaProducerFactory<>(
            producerProps, new StringSerializer(), new MessageSerializer());
    final KafkaTemplate<String, Message> template = new KafkaTemplate<>(producerFactory);
    template.setDefaultTopic(this.topic);
    template.send(this.topic, LocalDateTime.now().toString(), this.lowVoltageMessage);
  }

  private void theKafkaLoggerLogsTheLowVoltageData() throws Exception {
    final long timeout = 30;
    final TimeUnit timeUnit = TimeUnit.SECONDS;
    assertThat(this.countDownLatch.await(timeout, timeUnit))
        .overridingErrorMessage(
            "KafkaLogger did not receive a record to log within " + timeout + " " + timeUnit)
        .isTrue();
  }
}
