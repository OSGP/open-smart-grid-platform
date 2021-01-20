/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.opensmartgridplatform.adapter.kafka.da.application.repositories.LocationRepository;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
@EnableJpaRepositories(basePackageClasses = { LocationRepository.class })
@ComponentScan(
        basePackages = { "org.opensmartgridplatform.shared.domain.services", "org.opensmartgridplatform.shared.domain.entities",
                "org.opensmartgridplatform.domain.da",
                "org.opensmartgridplatform.adapter.kafka.da", "org.opensmartgridplatform.domain.logging",
                "org.opensmartgridplatform.domain.core.services", "org.opensmartgridplatform.shared.application.config",
                "org.opensmartgridplatform.adapter.kafka.da.infra.jms.messageprocessors" })
@Import({ PersistenceConfigCore.class, MessagingConfig.class })
@PropertySource("classpath:osgp-adapter-kafka-distributionautomation.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterKafkaDistributionAutomation/config}", ignoreResourceNotFound = true)
public class ApplicationContext extends AbstractPersistenceConfig {

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


    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        return super.getDataSource();
    }

    @Override
    @Bean(name = "transactionManager")
    public JpaTransactionManager transactionManager() {
        return super.transactionManager();
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return super.createFlyway();
    }

    @Override
    @DependsOn("flyway")
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        return super.entityManagerFactory("OSGP_ADAPTER_KAFKA_DISTRIBUTIONAUTOMATION");
    }
}
