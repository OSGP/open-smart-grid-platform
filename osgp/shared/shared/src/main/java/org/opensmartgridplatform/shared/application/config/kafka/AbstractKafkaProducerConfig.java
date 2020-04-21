/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config.kafka;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

public abstract class AbstractKafkaProducerConfig<T, U> extends AbstractKafkaConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractKafkaProducerConfig.class);

    protected KafkaTemplate<T, U> initKafkaTemplate(final String propertiesPrefix, final String topic) {

        LOGGER.debug("=================================================================================");
        LOGGER.info("Initializing KafkaTemplate for Topic {}", topic);
        LOGGER.debug("Common property prefix: {}", propertiesPrefix);
        LOGGER.debug("Producer property prefix: {}", this.getProducerPropertiesPrefix(propertiesPrefix));
        LOGGER.debug("=================================================================================");
        final Map<String, Object> producerConfigs = this.producerConfigs(propertiesPrefix);
        final KafkaTemplate<T, U> template = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfigs));
        template.setDefaultTopic(topic);
        return template;
    }

    protected Map<String, Object> producerConfigs(final String propertiesPrefix) {
        final Map<String, Object> properties = this.createCommonProperties(propertiesPrefix);
        KafkaProperties.producerProperties()
                .forEach((k, v) -> this.addIfExist(properties, k, this.getProducerPropertiesPrefix(propertiesPrefix),
                        v));
        return properties;
    }

    private String getProducerPropertiesPrefix(final String propertiesPrefix) {
        return propertiesPrefix + ".producer";
    }
}
