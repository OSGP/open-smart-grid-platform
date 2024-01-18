// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.kafka.logging.infra.kafka.in;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.kafka.logging.config.ApplicationContext;
import org.opensmartgridplatform.adapter.kafka.logging.config.StringMessageLoggingEnabled;
import org.opensmartgridplatform.adapter.kafka.logging.infra.kafka.in.StringMessageConsumerTest.TestConfig;
import org.opensmartgridplatform.kafka.logging.CountDownKafkaLogger;
import org.opensmartgridplatform.kafka.logging.KafkaLogger;
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
    topics = {"${distributionautomation.kafka.topic.message}"},
    brokerProperties = {
      "listeners=PLAINTEXT://${distributionautomation.kafka.bootstrap.servers}",
      "log.dirs=target/kafka-logs-distributionautomation-messages",
      "auto.create.topics.enable=true"
    })
@DirtiesContext
class StringMessageConsumerTest {

  /**
   * Spring test context configuration setting up the CountDownKafkaLogger to be used to verify
   * sending distribution automation message data leads to it being logged.
   *
   * @see CountDownKafkaLogger
   */
  @Configuration
  @Import(ApplicationContext.class)
  @Conditional(StringMessageLoggingEnabled.class)
  public static class TestConfig {

    @Bean
    public CountDownLatch countDownLatch() {
      return new CountDownLatch(1);
    }

    @Bean
    public KafkaLogger kafkaLogger(
        final CountDownLatch countDownLatch, final String distributionAutomationMessage) {
      return new CountDownKafkaLogger(countDownLatch, distributionAutomationMessage);
    }

    @Bean
    public String distributionAutomationMessage() {
      return "message-payload";
    }
  }

  @Value("${distributionautomation.kafka.topic.message}")
  private String topic;

  @Autowired private CountDownLatch countDownLatch;

  @Autowired private String distributionAutomationMessage;

  @Autowired private EmbeddedKafkaBroker broker;

  @Test
  void logsMessagesPostedToTheTopic() throws Exception {
    this.whenDistributionAutomationDataIsSentToTheTopic();
    this.theKafkaLoggerLogsTheDistributionAutomationData();
  }

  private void whenDistributionAutomationDataIsSentToTheTopic() {
    final var producerProps = KafkaTestUtils.producerProps(this.broker);
    final ProducerFactory<String, String> producerFactory =
        new DefaultKafkaProducerFactory<>(
            producerProps, new StringSerializer(), new StringSerializer());
    final var template = new KafkaTemplate<>(producerFactory);
    template.setDefaultTopic(this.topic);
    template.send(this.topic, LocalDateTime.now().toString(), this.distributionAutomationMessage);
  }

  private void theKafkaLoggerLogsTheDistributionAutomationData() throws Exception {
    final var timeout = 30L;
    final var timeUnit = TimeUnit.SECONDS;
    assertThat(this.countDownLatch.await(timeout, timeUnit))
        .overridingErrorMessage(
            "KafkaLogger did not receive a record to log within " + timeout + " " + timeUnit)
        .isTrue();
  }
}
