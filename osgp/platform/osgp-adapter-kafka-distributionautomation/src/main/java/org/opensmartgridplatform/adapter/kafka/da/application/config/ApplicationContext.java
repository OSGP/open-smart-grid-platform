/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.config;

import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
@ComponentScan(
        basePackages = { "org.opensmartgridplatform.shared.domain.services", "org.opensmartgridplatform.domain.da",
                "org.opensmartgridplatform.adapter.kafka.da", "org.opensmartgridplatform.domain.logging",
                "org.opensmartgridplatform.domain.core.services", "org.opensmartgridplatform.shared.application.config",
                "org.opensmartgridplatform.adapter.kafka.da.infra.jms.messageprocessors" })
@Import({ PersistenceConfigCore.class, MessagingConfig.class })
@PropertySource("classpath:osgp-adapter-kafka-distributionautomation.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterKafkaDistributionAutomation/config}", ignoreResourceNotFound = true)
public class ApplicationContext extends AbstractConfig {

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        final MethodValidationPostProcessor m = new MethodValidationPostProcessor();
        m.setValidatorFactory(this.validator());
        return m;
    }
}
