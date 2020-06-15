/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.logging.infra.kafka.in;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.kafka.da.avro.IntervalBlock;
import org.opensmartgridplatform.adapter.kafka.da.avro.IntervalReading;
import org.opensmartgridplatform.adapter.kafka.da.avro.MeterReading;
import org.opensmartgridplatform.adapter.kafka.da.avro.ReadingType;
import org.opensmartgridplatform.adapter.kafka.da.avro.UsagePoint;
import org.opensmartgridplatform.adapter.kafka.da.avro.ValuesInterval;
import org.opensmartgridplatform.adapter.kafka.da.serialization.MeterReadingSerializer;
import org.opensmartgridplatform.adapter.kafka.logging.config.ApplicationContext;
import org.opensmartgridplatform.adapter.kafka.logging.config.DistributionAutomationLoggingEnabled;
import org.opensmartgridplatform.adapter.kafka.logging.infra.kafka.in.MeterReadingConsumerTest.TestConfig;
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
@TestPropertySource("classpath:osgp-adapter-kafka-logging-test-distributionautomation.properties")
@EmbeddedKafka(partitions = 1,
        topics = { "${distributionautomation.kafka.topic}" },
        brokerProperties = { "listeners=PLAINTEXT://${distributionautomation.kafka.bootstrap.servers}",
                "log.dirs=target/kafka-logs-distributionautomation", "auto.create.topics.enable=true" })
@DirtiesContext
public class MeterReadingConsumerTest {

    /**
     * Spring test context configuration setting up the CountDownKafkaLogger to
     * be used to verify sending a meter reading leads to it being logged.
     *
     * @see CountDownKafkaLogger
     */
    @Configuration
    @Import(ApplicationContext.class)
    @Conditional(DistributionAutomationLoggingEnabled.class)
    public static class TestConfig {

        @Bean
        public CountDownLatch countDownLatch() {
            return new CountDownLatch(1);
        }

        @Bean
        public KafkaLogger kafkaLogger(final CountDownLatch countDownLatch, final MeterReading meterReading) {
            return new CountDownKafkaLogger(countDownLatch, meterReading);
        }

        @Bean
        public MeterReading meterReading() {
            return MeterReadingConsumerTest.createMeterReading();
        }
    }

    @Value("${distributionautomation.kafka.topic}")
    private String topic;

    @Autowired
    private EmbeddedKafkaBroker broker;

    @Autowired
    private CountDownLatch countDownLatch;

    @Autowired
    private MeterReading meterReading;

    @Test
    void logsMessagesPostedToTheDistributionAutomationTopic() throws Exception {
        this.whenMeterReadingDataIsSentToTheTopic();
        this.theKafkaLoggerLogsTheMeterReading();
    }

    private void whenMeterReadingDataIsSentToTheTopic() {
        final Map<String, Object> producerProps = KafkaTestUtils.producerProps(this.broker);
        final ProducerFactory<String, MeterReading> producerFactory = new DefaultKafkaProducerFactory<>(producerProps,
                new StringSerializer(), new MeterReadingSerializer());
        final KafkaTemplate<String, MeterReading> template = new KafkaTemplate<>(producerFactory);
        template.setDefaultTopic(this.topic);
        template.sendDefault(this.meterReading);
    }

    private void theKafkaLoggerLogsTheMeterReading() throws Exception {
        final long timeout = 30;
        final TimeUnit timeUnit = TimeUnit.SECONDS;
        assertThat(this.countDownLatch.await(timeout, timeUnit)).overridingErrorMessage(
                "KafkaLogger did not receive a record to log within " + timeout + " " + timeUnit).isTrue();
    }

    private static MeterReading createMeterReading() {
        final long end = System.currentTimeMillis();
        final long start = end - 60_000;
        final long time1 = end - 40_000;
        final long time2 = end - 20_000;
        final long time3 = end - 10_000;

        return MeterReading.newBuilder()
                .setMRid("mrid")
                .setName("meter reading")
                .setUsagePoint(UsagePoint.newBuilder().setMRid("mrid").build())
                .setIntervalBlocks(Arrays.asList(IntervalBlock.newBuilder()
                        .setIntervalReadings(Arrays.asList(
                                IntervalReading.newBuilder().setTimeStamp(start).setValue("start").build(),
                                IntervalReading.newBuilder().setTimeStamp(time1).setValue("first").build(),
                                IntervalReading.newBuilder().setTimeStamp(time2).setValue("second").build(),
                                IntervalReading.newBuilder().setTimeStamp(time3).setValue("third").build(),
                                IntervalReading.newBuilder().setTimeStamp(end).setValue("end").build()))
                        .setReadingType(ReadingType.newBuilder()
                                .setMeasuringKind("test")
                                .setMeasuringPeriod("1 minute")
                                .setMRid("mrid")
                                .setMultiplier(null)
                                .setName("text")
                                .setUnit(null)
                                .build())
                        .build()))
                .setValuesInterval(ValuesInterval.newBuilder().setStart(start).setEnd(end).build())
                .build();
    }

}
