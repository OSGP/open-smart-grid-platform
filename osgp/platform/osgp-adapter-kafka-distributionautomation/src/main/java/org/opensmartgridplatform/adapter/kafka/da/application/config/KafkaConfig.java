package org.opensmartgridplatform.adapter.kafka.da.application.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.opensmartgridplatform.adapter.kafka.MeterReading;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
// TODO - Enable when consumer is implemented
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@EnableKafka
@Configuration
public class KafkaConfig {
    @Resource
    private Environment environment;

    @Value("${distributionautomation.kafka.producer.topic}")
    private String topicProducer;

    // TODO - Enable when consumer is implemented
    // @Value("${distributionautomation.kafka.consumer.concurrency}")
    // private Integer concurrency;
    //
    // @Value("${distributionautomation.kafka.consumer.poll.timeout}")
    // private Integer pollTimeout;

    private Map<String, Object> producerConfigs() {
        final Map<String, Object> properties = this.createCommonProperties();
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer",
                "org.opensmartgridplatform.adapter.kafka.da.infra.kafka.out.MeterReadingSerializer");
        KafkaProperties.producerProperties()
                .forEach((k, v) -> this.addIfExist(properties, k, "distributionautomation.kafka.producer", v));
        return properties;
    }

    private Map<String, Object> createCommonProperties() {
        final Map<String, Object> properties = new HashMap<>();
        KafkaProperties.commonProperties()
                .forEach((k, v) -> this.addIfExist(properties, k, "distributionautomation.kafka", v));

        return properties;
    }

    // TODO - Enable when consumer is implemented
    // @formatter:off
//    @Bean
//    public Map<String, Object> consumerConfigs() {
//        final Map<String, Object> properties = this.createCommonProperties();
//
//        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        KafkaProperties.consumerProperties().forEach((k, v) -> this.addIfExist(properties, k, "kafka.consumer", v));
//        return properties;
//    }
    // @formatter:on

    private <T> void addIfExist(final Map<String, Object> properties, final String kafkaProperty, final String prefix,
            final Class<T> targetType) {
        final String fullPropertyName = prefix + "." + kafkaProperty;
        final T value = this.environment.getProperty(fullPropertyName, targetType);
        if (value != null) {
            properties.put(kafkaProperty, value);
        }
    }

    @Bean
    public KafkaTemplate<String, MeterReading> kafkaTemplate() {
        final KafkaTemplate<String, MeterReading> template = new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(this.producerConfigs()));
        template.setDefaultTopic(this.topicProducer);
        return template;
    }

    // TODO - Enable when consumer is implemented
    // @formatter:off
//    @Bean
//    public ConsumerFactory<String, String> consumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(this.consumerConfigs());
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
//        final ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(this.consumerFactory());
//        factory.setConcurrency(this.concurrency);
//        factory.getContainerProperties().setPollTimeout(this.pollTimeout);
//        return factory;
//    }
    // @formatter:on

}
