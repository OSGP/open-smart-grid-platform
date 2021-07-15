/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.application.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.opensmartgridplatform.webdevicesimulator.domain.repositories.DeviceRepository;
import org.opensmartgridplatform.webdevicesimulator.service.RegisterDevice;
import org.opensmartgridplatform.webdevicesimulator.service.SwitchingServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * An application context Java configuration class. The usage of Java configuration requires Spring
 * Framework 3.0 or higher with following exceptions:
 *
 * <ul>
 *   <li>@EnableWebMvc annotation requires Spring Framework 3.1
 * </ul>
 */
@Configuration
@ComponentScan(basePackages = {"org.opensmartgridplatform.webdevicesimulator"})
@EnableJpaRepositories(
    entityManagerFactoryRef = "entityManagerFactory",
    basePackageClasses = {DeviceRepository.class})
@EnableTransactionManagement
@EnableWebMvc
@ImportResource("classpath:applicationContext.xml")
@PropertySource("classpath:web-device-simulator.properties")
@PropertySource(value = "file:${osgp/WebDeviceSimulator/config}", ignoreResourceNotFound = true)
public class ApplicationContext {

  private static final String VIEW_RESOLVER_PREFIX = "/WEB-INF/views/";
  private static final String VIEW_RESOLVER_SUFFIX = ".jsp";

  private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
  private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
  private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY =
      "hibernate.physical_naming_strategy";
  private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";

  @Value("${db.driver}")
  private String driver;

  @Value("${db.username}")
  private String username;

  @Value("${db.password}")
  private String password;

  @Value("${db.url}")
  private String url;

  @Value("${db.max_pool_size:5}")
  private int maxPoolSize;

  @Value("${db.auto_commit:false}")
  private boolean autoCommit;

  @Value("${hibernate.dialect}")
  private String hibernateDialect;

  @Value("${hibernate.format_sql}")
  private String hibernateFormatSql;

  @Value("${hibernate.physical_naming_strategy}")
  private String hibernatePhysicalStategy;

  @Value("${hibernate.show_sql}")
  private String hibernateShowSql;

  @Value("${flyway.initial.version}")
  private String flywayInitialVersion;

  @Value("${flyway.initial.description}")
  private String flywayInitialDescription;

  @Value("${flyway.init.on.migrate:true}")
  private boolean flywayInitOnMigrate;

  @Value("${entitymanager.packages.to.scan}")
  private String entitymanagerPackagesToScan;

  @Value("${message.source.basename}")
  private String messageSourceBasename;

  @Value("${message.source.use.code.as.default.message:true}")
  private boolean messageSourceUseCodeAsDefaultMessage;

  @Value("${reboot.delay.seconds:5}")
  private int rebootDelayInSeconds;

  @Value("${response.delay.time:10}")
  private long responseDelayTime;

  @Value("${response.delay.random.range:20}")
  private long responseDelayRandomRange;

  @Value("${checkbox.device.registration.value}")
  private boolean checkboxDeviceRegistration;

  @Value("${checkbox.device.reboot.value}")
  private boolean checkboxDeviceReboot;

  @Value("${checkbox.light.switching.value:false}")
  private boolean checkboxLightSwitching;

  @Value("${checkbox.tariff.switching.value:false}")
  private boolean checkboxTariffSwitching;

  @Value("${checkbox.event.notification.value:false}")
  private boolean checkboxEventNotification;

  @Value("${configuration.ip.config.fixed.ip.address}")
  private String ipConfigFixedIpAddress;

  @Value("${configuration.ip.config.netmask}")
  private String ipConfigNetmask;

  @Value("${configuration.ip.config.gateway}")
  private String ipConfigGateway;

  @Value("${configuration.osgp.ip.address}")
  private String osgpIpAddress;

  @Value("${configuration.osgp.port.number:12122}")
  private int osgpPortNumber;

  @Value("${status.internal.ip.address}")
  private String statusInternalIpAddress;

  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

  private HikariDataSource dataSource;

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  /**
   * Method for creating the Data Source.
   *
   * @return DataSource
   */
  public DataSource getDataSource() {
    if (this.dataSource == null) {
      final HikariConfig hikariConfig = new HikariConfig();

      hikariConfig.setDriverClassName(this.driver);
      hikariConfig.setJdbcUrl(this.url);
      hikariConfig.setUsername(this.username);
      hikariConfig.setPassword(this.password);

      hikariConfig.setMaximumPoolSize(this.maxPoolSize);
      hikariConfig.setAutoCommit(this.autoCommit);

      this.dataSource = new HikariDataSource(hikariConfig);
    }
    return this.dataSource;
  }

  /**
   * Method for creating the Transaction Manager.
   *
   * @return JpaTransactionManager
   */
  @Bean
  public JpaTransactionManager transactionManager() {
    final JpaTransactionManager transactionManager = new JpaTransactionManager();

    transactionManager.setEntityManagerFactory(this.entityManagerFactory().getObject());

    return transactionManager;
  }

  @Bean(initMethod = "migrate")
  public Flyway flyway() {
    return Flyway.configure()
        .baselineVersion(MigrationVersion.fromVersion(this.flywayInitialVersion))
        .baselineDescription(this.flywayInitialDescription)
        .baselineOnMigrate(this.flywayInitOnMigrate)
        .outOfOrder(true)
        .table("schema_version")
        .dataSource(this.getDataSource())
        .load();
  }

  /**
   * Method for creating the Entity Manager Factory Bean.
   *
   * @return LocalContainerEntityManagerFactoryBean
   */
  @Bean
  @DependsOn("flyway")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
        new LocalContainerEntityManagerFactoryBean();

    entityManagerFactoryBean.setPersistenceUnitName("OSPG_DEVICESIMULATOR_WEB");
    entityManagerFactoryBean.setDataSource(this.getDataSource());
    entityManagerFactoryBean.setPackagesToScan(this.entitymanagerPackagesToScan);
    entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);

    final Properties jpaProperties = new Properties();
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_DIALECT, this.hibernateDialect);
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL, this.hibernateFormatSql);
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY, this.hibernatePhysicalStategy);
    jpaProperties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, this.hibernateShowSql);

    entityManagerFactoryBean.setJpaProperties(jpaProperties);

    return entityManagerFactoryBean;
  }

  /**
   * Method for creating the Message Source.
   *
   * @return MessageSource
   */
  @Bean
  public MessageSource messageSource() {
    final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

    messageSource.setBasename(this.messageSourceBasename);
    messageSource.setUseCodeAsDefaultMessage(this.messageSourceUseCodeAsDefaultMessage);

    return messageSource;
  }

  /**
   * Method for resolving views.
   *
   * @return ViewResolver
   */
  @Bean
  public ViewResolver viewResolver() {
    final InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();

    viewResolver.setViewClass(JstlView.class);
    viewResolver.setPrefix(VIEW_RESOLVER_PREFIX);
    viewResolver.setSuffix(VIEW_RESOLVER_SUFFIX);

    return viewResolver;
  }

  @Bean
  public RegisterDevice registerDevice() {
    return new RegisterDevice();
  }

  @Bean
  public SwitchingServices switchingServices() {
    return new SwitchingServices();
  }

  @Bean
  public Integer rebootDelayInSeconds() {
    return this.rebootDelayInSeconds;
  }

  @Bean
  public Long responseDelayTime() {

    LOGGER.info("response delay time in milliseconds: {}", this.responseDelayTime);

    return this.responseDelayTime;
  }

  @Bean
  public Long reponseDelayRandomRange() {

    LOGGER.info("response end delay time in milliseconds: {}", this.responseDelayRandomRange);

    return this.responseDelayRandomRange;
  }

  @Bean
  public Boolean checkboxDeviceRegistrationValue() {
    return this.checkboxDeviceRegistration;
  }

  @Bean
  public Boolean checkboxDeviceRebootValue() {
    return this.checkboxDeviceReboot;
  }

  @Bean
  public Boolean checkboxLightSwitchingValue() {
    return this.checkboxLightSwitching;
  }

  @Bean
  public Boolean checkboxTariffSwitchingValue() {
    return this.checkboxTariffSwitching;
  }

  @Bean
  public Boolean checkboxEventNotificationValue() {
    return this.checkboxEventNotification;
  }

  @Bean
  public String configurationIpConfigFixedIpAddress() {
    return this.ipConfigFixedIpAddress;
  }

  @Bean
  public String configurationIpConfigNetmask() {
    return this.ipConfigNetmask;
  }

  @Bean
  public String configurationIpConfigGateway() {
    return this.ipConfigGateway;
  }

  @Bean
  public String configurationOsgpIpAddress() {
    return this.osgpIpAddress;
  }

  @Bean
  public Integer configurationOsgpPortNumber() {
    return this.osgpPortNumber;
  }

  @Bean
  public String statusInternalIpAddress() {
    return this.statusInternalIpAddress;
  }

  @PreDestroy
  public void destroyDataSource() {
    if (this.dataSource != null) {
      this.dataSource.close();
    }
  }
}
