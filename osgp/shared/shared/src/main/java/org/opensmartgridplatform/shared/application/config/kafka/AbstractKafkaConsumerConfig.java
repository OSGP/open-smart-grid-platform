/** Copyright 2020 Smart Society Services B.V. */
package org.opensmartgridplatform.shared.application.config.kafka;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

public abstract class AbstractKafkaConsumerConfig<K, V> extends KafkaConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractKafkaConsumerConfig.class);

  private ConsumerFactory<K, V> consumerFactory;
  private ConcurrentKafkaListenerContainerFactory<K, V> kafkaListenerContainerFactory;

  public AbstractKafkaConsumerConfig(
      final Environment environment,
      final String propertiesPrefix,
      final String topic,
      final int concurrency,
      final int pollTimeout) {
    super(environment);

    LOGGER.debug(
        "=================================================================================");
    LOGGER.info("Initializing Kafka Consumer for Topic {}", topic);
    LOGGER.debug("Common properties prefix: {}", propertiesPrefix);
    LOGGER.debug("Consumer properties prefix: {}", getConsumerPropertiesPrefix(propertiesPrefix));
    LOGGER.debug(
        "=================================================================================");

    this.initKafkaConsumer(propertiesPrefix, concurrency, pollTimeout);
  }

  public abstract ConsumerFactory<K, V> consumerFactory();

  public abstract ConcurrentKafkaListenerContainerFactory<K, V> kafkaListenerContainerFactory();

  protected ConsumerFactory<K, V> getConsumerFactory() {
    return this.consumerFactory;
  }

  protected ConcurrentKafkaListenerContainerFactory<K, V> getKafkaListenerContainerFactory() {
    return this.kafkaListenerContainerFactory;
  }

  private static String getConsumerPropertiesPrefix(final String propertiesPrefix) {
    return propertiesPrefix + ".consumer";
  }

  private void initKafkaConsumer(
      final String propertiesPrefix, final int concurrency, final int pollTimeout) {
    final Map<String, Object> consumerConfigs = this.consumerConfigs(propertiesPrefix);
    this.consumerFactory = new DefaultKafkaConsumerFactory<>(consumerConfigs);
    this.kafkaListenerContainerFactory =
        this.initKafkaListenerContainerFactory(concurrency, pollTimeout);
  }

  private ConcurrentKafkaListenerContainerFactory<K, V> initKafkaListenerContainerFactory(
      final int concurrency, final int pollTimeout) {
    final ConcurrentKafkaListenerContainerFactory<K, V> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(this.consumerFactory);
    factory.setConcurrency(concurrency);
    factory.getContainerProperties().setPollTimeout(pollTimeout);
    return factory;
  }

  private Map<String, Object> consumerConfigs(final String propertiesPrefix) {
    final Map<String, Object> properties = this.createCommonProperties(propertiesPrefix);
    KafkaProperties.consumerProperties()
        .forEach(
            (k, v) ->
                this.addIfExist(properties, k, getConsumerPropertiesPrefix(propertiesPrefix), v));
    return properties;
  }
}
