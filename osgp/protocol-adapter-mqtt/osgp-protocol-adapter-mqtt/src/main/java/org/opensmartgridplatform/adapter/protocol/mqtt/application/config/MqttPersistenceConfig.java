/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.opensmartgridplatform.adapter.protocol.mqtt.domain.repositories.MqttDeviceRepository;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;
import org.opensmartgridplatform.shared.infra.db.DefaultConnectionPoolFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJpaRepositories(
    entityManagerFactoryRef = "mqttEntityManagerFactory",
    basePackageClasses = {MqttDeviceRepository.class})
@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:osgp-adapter-protocol-mqtt.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolMqtt/config}", ignoreResourceNotFound = true)
public class MqttPersistenceConfig extends AbstractPersistenceConfig {

  @Value("${db.username.mqtt}")
  private String username;

  @Value("${db.password.mqtt}")
  private String password;

  @Value("${db.host.mqtt}")
  private String databaseHost;

  @Value("${db.port.mqtt}")
  private int databasePort;

  @Value("${db.name.mqtt}")
  private String databaseName;

  private HikariDataSource dataSourceMqtt;

  public DataSource getDataSourceMqtt() {
    if (this.dataSourceMqtt == null) {
      final DefaultConnectionPoolFactory.Builder builder =
          super.builder()
              .withUsername(this.username)
              .withPassword(this.password)
              .withDatabaseHost(this.databaseHost)
              .withDatabasePort(this.databasePort)
              .withDatabaseName(this.databaseName);
      final DefaultConnectionPoolFactory factory = builder.build();
      this.dataSourceMqtt = factory.getDefaultConnectionPool();
    }
    return this.dataSourceMqtt;
  }

  @Override
  @Bean
  public JpaTransactionManager transactionManager() {
    return super.transactionManager();
  }

  @Bean(initMethod = "migrate")
  public Flyway mqttFlyway() {
    return super.createFlyway(this.getDataSourceMqtt());
  }

  @Override
  @Bean(name = "mqttEntityManagerFactory")
  @DependsOn("mqttFlyway")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    return super.entityManagerFactory(
        "OSGP_PROTOCOL_ADAPTER_MQTT_SETTINGS", this.getDataSourceMqtt());
  }

  @Override
  @PreDestroy
  public void destroyDataSource() {
    if (this.dataSourceMqtt != null) {
      this.dataSourceMqtt.close();
    }
  }
}
