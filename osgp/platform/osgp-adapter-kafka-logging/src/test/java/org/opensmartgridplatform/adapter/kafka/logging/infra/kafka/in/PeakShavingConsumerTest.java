/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.logging.infra.kafka.in;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.kafka.logging.config.ApplicationContext;
import org.opensmartgridplatform.adapter.kafka.logging.config.PeakShavingLoggingEnabled;
import org.opensmartgridplatform.adapter.kafka.logging.infra.kafka.in.PeakShavingConsumerTest.TestConfig;
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
@TestPropertySource("classpath:osgp-adapter-kafka-logging-test-peakshaving.properties")
@EmbeddedKafka(partitions = 1,
        topics = { "${peakshaving.kafka.topic}" },
        brokerProperties = { "listeners=PLAINTEXT://${peakshaving.kafka.bootstrap.servers}",
                "log.dirs=target/kafka-logs-peakshaving", "auto.create.topics.enable=true" })
@DirtiesContext
public class PeakShavingConsumerTest {

    /**
     * Spring test context configuration setting up the CountDownKafkaLogger to
     * be used to verify sending peak shaving data leads to it being logged.
     *
     * @see CountDownKafkaLogger
     */
    @Configuration
    @Import(ApplicationContext.class)
    @Conditional(PeakShavingLoggingEnabled.class)
    public static class TestConfig {

        @Bean
        public CountDownLatch countDownLatch() {
            return new CountDownLatch(1);
        }

        @Bean
        public KafkaLogger kafkaLogger(final CountDownLatch countDownLatch, final String peakShavingData) {
            return new CountDownKafkaLogger(countDownLatch, peakShavingData);
        }

        @Bean
        public String peakShavingData() {
            return "TST-01; 220.1; 220.2; 220.3; 5.1; 5.2; 5.3; 7.1; 7.2; 7.3;";
        }
    }

    @Value("${peakshaving.kafka.topic}")
    private String topic;

    @Autowired
    private CountDownLatch countDownLatch;

    @Autowired
    private String peakShavingData;

    @Autowired
    private EmbeddedKafkaBroker broker;

    @Test
    void logsMessagesPostedToThePeakShavingTopic() throws Exception {
        this.whenPeakShavingDataIsSentToTheTopic();
        this.theKafkaLoggerLogsThePeakShavingData();
    }

    private void whenPeakShavingDataIsSentToTheTopic() {
        final Map<String, Object> producerProps = KafkaTestUtils.producerProps(this.broker);
        final ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(producerProps,
                new StringSerializer(), new StringSerializer());
        final KafkaTemplate<String, String> template = new KafkaTemplate<>(producerFactory);
        template.setDefaultTopic(this.topic);
        template.send(this.topic, LocalDateTime.now().toString(), this.peakShavingData);
    }

    private void theKafkaLoggerLogsThePeakShavingData() throws Exception {
        final long timeout = 30;
        final TimeUnit timeUnit = TimeUnit.SECONDS;
        assertThat(this.countDownLatch.await(timeout, timeUnit)).overridingErrorMessage(
                "KafkaLogger did not receive a record to log within " + timeout + " " + timeUnit).isTrue();
    }
}
