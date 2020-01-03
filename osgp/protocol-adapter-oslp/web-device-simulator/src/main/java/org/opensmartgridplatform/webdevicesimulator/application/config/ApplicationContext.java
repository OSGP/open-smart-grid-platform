/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.application.config;

import java.util.Properties;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.opensmartgridplatform.webdevicesimulator.domain.repositories.DeviceRepository;
import org.opensmartgridplatform.webdevicesimulator.service.RegisterDevice;
import org.opensmartgridplatform.webdevicesimulator.service.SwitchingServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0 or higher with following
 * exceptions:
 * <ul>
 * <li>@EnableWebMvc annotation requires Spring Framework 3.1</li>
 * </ul>
 */
@Configuration
@ComponentScan(basePackages = { "org.opensmartgridplatform.webdevicesimulator" })
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactory",
        basePackageClasses = { DeviceRepository.class })
@EnableTransactionManagement
@EnableWebMvc
@ImportResource("classpath:applicationContext.xml")
@PropertySource("classpath:web-device-simulator.properties")
@PropertySource(value = "file:${osgp/WebDeviceSimulator/config}", ignoreResourceNotFound = true)
public class ApplicationContext {

    private static final String VIEW_RESOLVER_PREFIX = "/WEB-INF/views/";
    private static final String VIEW_RESOLVER_SUFFIX = ".jsp";

    private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
    private static final String PROPERTY_NAME_DATABASE_PW = "db.password";
    private static final String PROPERTY_NAME_DATABASE_URL = "db.url";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";

    private static final String PROPERTY_NAME_DATABASE_MAX_POOL_SIZE = "db.max_pool_size";
    private static final String PROPERTY_NAME_DATABASE_AUTO_COMMIT = "db.auto_commit";

    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.physical_naming_strategy";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";

    private static final String PROPERTY_NAME_FLYWAY_INITIAL_VERSION = "flyway.initial.version";
    private static final String PROPERTY_NAME_FLYWAY_INITIAL_DESCRIPTION = "flyway.initial.description";
    private static final String PROPERTY_NAME_FLYWAY_INIT_ON_MIGRATE = "flyway.init.on.migrate";

    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";

    private static final String PROPERTY_NAME_MESSAGESOURCE_BASENAME = "message.source.basename";
    private static final String PROPERTY_NAME_MESSAGESOURCE_USE_CODE_AS_DEFAULT_MESSAGE = "message.source.use.code.as.default.message";

    private static final String PROPERTY_NAME_RESPONSE_DELAY_TIME = "response.delay.time";
    private static final String PROPERTY_NAME_RESPONSE_DELAY_RANDOM_RANGE = "response.delay.random.range";

    private static final String PROPERTY_NAME_CHECKBOX_DEVICE_REGISTRATION_VALUE = "checkbox.device.registration.value";
    private static final String PROPERTY_NAME_CHECKBOX_DEVICE_REBOOT_VALUE = "checkbox.device.reboot.value";
    private static final String PROPERTY_NAME_CHECKBOX_LIGHT_SWITCHING_VALUE = "checkbox.light.switching.value";
    private static final String PROPERTY_NAME_CHECKBOX_TARIFF_SWITCHING_VALUE = "checkbox.tariff.switching.value";
    private static final String PROPERTY_NAME_CHECKBOX_EVENT_NOTIFICATION_VALUE = "checkbox.event.notification.value";

    private static final String PROPERTY_NAME_FIRMWARE_VERSION = "firmware.version";

    private static final String PROPERTY_NAME_CONFIGURATION_IP_CONFIG_FIXED_IP_ADDRESS = "configuration.ip.config.fixed.ip.address";
    private static final String PROPERTY_NAME_CONFIGURATION_IP_CONFIG_NETMASK = "configuration.ip.config.netmask";
    private static final String PROPERTY_NAME_CONFIGURATION_IP_CONFIG_GATEWAY = "configuration.ip.config.gateway";
    private static final String PROPERTY_NAME_CONFIGURATION_OSGP_IP_ADDRESS = "configuration.osgp.ip.address";
    private static final String PROPERTY_NAME_CONFIGURATION_OSGP_PORT_NUMBER = "configuration.osgp.port.number";

    private static final String PROPERTY_NAME_STATUS_INTERNAL_IP_ADDRESS = "status.internal.ip.address";

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

    @Resource
    private Environment environment;

    private HikariDataSource dataSource;

    /**
     * Method for creating the Data Source.
     *
     * @return DataSource
     */
    public DataSource getDataSource() {
        if (this.dataSource == null) {
            final HikariConfig hikariConfig = new HikariConfig();

            hikariConfig.setDriverClassName(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
            hikariConfig.setJdbcUrl(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
            hikariConfig.setUsername(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
            hikariConfig.setPassword(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_PW));

            hikariConfig.setMaximumPoolSize(
                    Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_MAX_POOL_SIZE)));
            hikariConfig.setAutoCommit(
                    Boolean.parseBoolean(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_AUTO_COMMIT)));

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
        final Flyway flyway = new Flyway();

        // Initialization for non-empty schema with no metadata table
        flyway.setBaselineVersion(MigrationVersion
                .fromVersion(this.environment.getRequiredProperty(PROPERTY_NAME_FLYWAY_INITIAL_VERSION)));
        flyway.setBaselineDescription(this.environment.getRequiredProperty(PROPERTY_NAME_FLYWAY_INITIAL_DESCRIPTION));
        flyway.setBaselineOnMigrate(
                Boolean.parseBoolean(this.environment.getRequiredProperty(PROPERTY_NAME_FLYWAY_INIT_ON_MIGRATE)));
        flyway.setTable("schema_version");

        flyway.setDataSource(this.getDataSource());

        return flyway;
    }

    /**
     * Method for creating the Entity Manager Factory Bean.
     *
     * @return LocalContainerEntityManagerFactoryBean
     */
    @Bean
    @DependsOn("flyway")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setPersistenceUnitName("OSPG_DEVICESIMULATOR_WEB");
        entityManagerFactoryBean.setDataSource(this.getDataSource());
        entityManagerFactoryBean
                .setPackagesToScan(this.environment.getRequiredProperty(PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN));
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);

        final Properties jpaProperties = new Properties();
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_DIALECT,
                this.environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL,
                this.environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL));
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY,
                this.environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY));
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL,
                this.environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));

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

        messageSource.setBasename(this.environment.getRequiredProperty(PROPERTY_NAME_MESSAGESOURCE_BASENAME));
        messageSource.setUseCodeAsDefaultMessage(Boolean.parseBoolean(
                this.environment.getRequiredProperty(PROPERTY_NAME_MESSAGESOURCE_USE_CODE_AS_DEFAULT_MESSAGE)));

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
    public Long responseDelayTime() {
        final String propertyValue = this.environment.getProperty(PROPERTY_NAME_RESPONSE_DELAY_TIME);

        final Long value = propertyValue == null ? null : Long.parseLong(propertyValue);
        if (value == null) {
            LOGGER.info("response delay time in milliseconds is not set using property: {}",
                    PROPERTY_NAME_RESPONSE_DELAY_TIME);
        } else {

            LOGGER.info("response delay time in milliseconds: {}", value);
        }

        return value;
    }

    @Bean
    public Long reponseDelayRandomRange() {
        final String propertyValue = this.environment.getProperty(PROPERTY_NAME_RESPONSE_DELAY_RANDOM_RANGE);

        final Long value = propertyValue == null ? null : Long.parseLong(propertyValue);
        if (value == null) {
            LOGGER.info("response end delay time in milliseconds is not set using property: {}",
                    PROPERTY_NAME_RESPONSE_DELAY_RANDOM_RANGE);
        } else {

            LOGGER.info("response end delay time in milliseconds: {}", value);
        }

        return value;
    }

    @Bean
    public Boolean checkboxDeviceRegistrationValue() {
        return Boolean
                .parseBoolean(this.environment.getRequiredProperty(PROPERTY_NAME_CHECKBOX_DEVICE_REGISTRATION_VALUE));
    }

    @Bean
    public Boolean checkboxDeviceRebootValue() {
        return Boolean.parseBoolean(this.environment.getRequiredProperty(PROPERTY_NAME_CHECKBOX_DEVICE_REBOOT_VALUE));
    }

    @Bean
    public Boolean checkboxLightSwitchingValue() {
        return Boolean.parseBoolean(this.environment.getRequiredProperty(PROPERTY_NAME_CHECKBOX_LIGHT_SWITCHING_VALUE));
    }

    @Bean
    public Boolean checkboxTariffSwitchingValue() {
        return Boolean
                .parseBoolean(this.environment.getRequiredProperty(PROPERTY_NAME_CHECKBOX_TARIFF_SWITCHING_VALUE));
    }

    @Bean
    public Boolean checkboxEventNotificationValue() {
        return Boolean
                .parseBoolean(this.environment.getRequiredProperty(PROPERTY_NAME_CHECKBOX_EVENT_NOTIFICATION_VALUE));
    }

    @Bean
    public String firmwareVersion() {
        return this.environment.getRequiredProperty(PROPERTY_NAME_FIRMWARE_VERSION);
    }

    @Bean
    public String configurationIpConfigFixedIpAddress() {
        return this.environment.getRequiredProperty(PROPERTY_NAME_CONFIGURATION_IP_CONFIG_FIXED_IP_ADDRESS);
    }

    @Bean
    public String configurationIpConfigNetmask() {
        return this.environment.getRequiredProperty(PROPERTY_NAME_CONFIGURATION_IP_CONFIG_NETMASK);
    }

    @Bean
    public String configurationIpConfigGateway() {
        return this.environment.getRequiredProperty(PROPERTY_NAME_CONFIGURATION_IP_CONFIG_GATEWAY);
    }

    @Bean
    public String configurationOsgpIpAddress() {
        return this.environment.getRequiredProperty(PROPERTY_NAME_CONFIGURATION_OSGP_IP_ADDRESS);
    }

    @Bean
    public Integer configurationOsgpPortNumber() {
        return Integer.valueOf(this.environment.getRequiredProperty(PROPERTY_NAME_CONFIGURATION_OSGP_PORT_NUMBER));
    }

    @Bean
    public String statusInternalIpAddress() {
        return this.environment.getRequiredProperty(PROPERTY_NAME_STATUS_INTERNAL_IP_ADDRESS);
    }

    @PreDestroy
    public void destroyDataSource() {
        if (this.dataSource != null) {
            this.dataSource.close();
        }
    }
}
