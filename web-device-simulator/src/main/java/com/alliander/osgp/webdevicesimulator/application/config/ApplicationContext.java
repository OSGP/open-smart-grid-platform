/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.webdevicesimulator.application.config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.sql.DataSource;

import org.hibernate.ejb.HibernatePersistence;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
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
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.alliander.osgp.oslp.OslpDecoder;
import com.alliander.osgp.oslp.OslpEncoder;
import com.alliander.osgp.shared.security.CertificateHelper;
import com.alliander.osgp.webdevicesimulator.service.OslpChannelHandler;
import com.alliander.osgp.webdevicesimulator.service.OslpSecurityHandler;
import com.alliander.osgp.webdevicesimulator.service.RegisterDevice;
import com.alliander.osgp.webdevicesimulator.service.SwitchingServices;
import com.googlecode.flyway.core.Flyway;
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
@ComponentScan(basePackages = { "com.alliander.osgp.webdevicesimulator" })
@EnableWebMvc
@ImportResource("classpath:applicationContext.xml")
@PropertySource("file:${osp/webDeviceSimulator/config}")
public class ApplicationContext {

    private static final String VIEW_RESOLVER_PREFIX = "/WEB-INF/views/";
    private static final String VIEW_RESOLVER_SUFFIX = ".jsp";

    private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
    private static final String PROPERTY_NAME_DATABASE_URL = "db.url";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";

    private static final String PROPERTY_NAME_DATABASE_MAX_POOL_SIZE = "db.max_pool_size";
    private static final String PROPERTY_NAME_DATABASE_AUTO_COMMIT = "db.auto_commit";

    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";

    private static final String PROPERTY_NAME_FLYWAY_INITIAL_VERSION = "flyway.initial.version";
    private static final String PROPERTY_NAME_FLYWAY_INITIAL_DESCRIPTION = "flyway.initial.description";
    private static final String PROPERTY_NAME_FLYWAY_INIT_ON_MIGRATE = "flyway.init.on.migrate";

    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";

    private static final String PROPERTY_NAME_MESSAGESOURCE_BASENAME = "message.source.basename";
    private static final String PROPERTY_NAME_MESSAGESOURCE_USE_CODE_AS_DEFAULT_MESSAGE = "message.source.use.code.as.default.message";

    private static final String PROPERTY_NAME_OSLP_TIMEOUT_CONNECT = "oslp.timeout.connect";

    private static final String PROPERTY_NAME_OSLP_PORT_CLIENT = "oslp.port.client";
    private static final String PROPERTY_NAME_OSLP_ELSTER_PORT_CLIENT = "oslp.elster.port.client";
    private static final String PROPERTY_NAME_OSLP_PORT_SERVER = "oslp.port.server";
    private static final String PROPERTY_NAME_OSLP_ELSTER_PORT_SERVER = "oslp.elster.port.server";
    private static final String PROPERTY_NAME_OSLP_ADDRESS_CLIENT = "oslp.address.client";

    private static final String PROPERTY_NAME_OSLP_SECURITY_SIGNKEY_PATH = "oslp.security.signkey.path";
    private static final String PROPERTY_NAME_OSLP_SECURITY_VERIFYKEY_PATH = "oslp.security.verifykey.path";
    private static final String PROPERTY_NAME_OSLP_SECURITY_KEYTYPE = "oslp.security.keytype";
    private static final String PROPERTY_NAME_OSLP_SECURITY_SIGNATURE = "oslp.security.signature";
    private static final String PROPERTY_NAME_OSLP_SECURITY_PROVIDER = "oslp.security.provider";
    private static final String PROPERTY_NAME_OSLP_SEQUENCE_NUMBER_WINDOW = "oslp.sequence.number.window";
    private static final String PROPERTY_NAME_OSLP_SEQUENCE_NUMBER_MAXIMUM = "oslp.sequence.number.maximum";

    private static final String PROPERTY_NAME_RESPONSE_DELAY_TIME = "response.delay.time";
    private static final String PROPERTY_NAME_RESPONSE_DELAY_RANDOM_RANGE = "response.delay.random.range";

    private static final String PROPERTY_NAME_CHECKBOX_DEVICE_REGISTRATION_VALUE = "checkbox.device.registration.value";
    private static final String PROPERTY_NAME_CHECKBOX_DEVICE_REBOOT_VALUE = "checkbox.device.reboot.value";
    private static final String PROPERTY_NAME_CHECKBOX_LIGHT_SWITCHING_VALUE = "checkbox.light.switching.value";
    private static final String PROPERTY_NAME_CHECKBOX_TARIFF_SWITCHING_VALUE = "checkbox.tariff.switching.value";
    private static final String PROPERTY_NAME_CHECKBOX_EVENT_NOTIFICATION_VALUE = "checkbox.event.notification.value";

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
            hikariConfig.setPassword(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));

            hikariConfig.setMaximumPoolSize(Integer.parseInt(this.environment
                    .getRequiredProperty(PROPERTY_NAME_DATABASE_MAX_POOL_SIZE)));
            hikariConfig.setAutoCommit(Boolean.parseBoolean(this.environment
                    .getRequiredProperty(PROPERTY_NAME_DATABASE_AUTO_COMMIT)));

            this.dataSource = new HikariDataSource(hikariConfig);
        }
        return this.dataSource;
    }

    /**
     * Method for creating the Transaction Manager.
     *
     * @return JpaTransactionManager
     * @throws ClassNotFoundException
     *             when class not found
     */
    @Bean
    public JpaTransactionManager transactionManager() throws ClassNotFoundException {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();

        transactionManager.setEntityManagerFactory(this.entityManagerFactory().getObject());

        return transactionManager;
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        final Flyway flyway = new Flyway();

        // Initialization for non-empty schema with no metadata table
        flyway.setInitVersion(this.environment.getRequiredProperty(PROPERTY_NAME_FLYWAY_INITIAL_VERSION));
        flyway.setInitDescription(this.environment.getRequiredProperty(PROPERTY_NAME_FLYWAY_INITIAL_DESCRIPTION));
        flyway.setInitOnMigrate(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_FLYWAY_INIT_ON_MIGRATE)));

        flyway.setDataSource(this.getDataSource());

        return flyway;
    }

    /**
     * Method for creating the Entity Manager Factory Bean.
     *
     * @return LocalContainerEntityManagerFactoryBean
     * @throws ClassNotFoundException
     *             when class not found
     */
    @Bean
    @DependsOn("flyway")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws ClassNotFoundException {
        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setPersistenceUnitName("OSPG_DEVICESIMULATOR_WEB");
        entityManagerFactoryBean.setDataSource(this.getDataSource());
        entityManagerFactoryBean.setPackagesToScan(this.environment
                .getRequiredProperty(PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN));
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistence.class);

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
        messageSource.setUseCodeAsDefaultMessage(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_MESSAGESOURCE_USE_CODE_AS_DEFAULT_MESSAGE)));

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

    @Bean(destroyMethod = "releaseExternalResources")
    public ClientBootstrap clientBootstrap() {
        final ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        final ChannelPipelineFactory pipelineFactory = new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException,
            NoSuchProviderException {
                final ChannelPipeline pipeline = ApplicationContext.this.createPipeLine();

                LOGGER.info("Created new client pipeline");

                return pipeline;
            }
        };

        final ClientBootstrap bootstrap = new ClientBootstrap(factory);

        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", false);
        bootstrap.setOption("connectTimeoutMillis", this.connectionTimeout());

        bootstrap.setPipelineFactory(pipelineFactory);

        return bootstrap;
    }

    @Bean(destroyMethod = "releaseExternalResources")
    public ServerBootstrap serverBootstrap() {
        final ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        final ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException,
            NoSuchProviderException {
                final ChannelPipeline pipeline = ApplicationContext.this.createPipeLine();
                LOGGER.info("Created new server pipeline");

                return pipeline;
            }
        });

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", false);

        bootstrap.bind(new InetSocketAddress(this.oslpPortServer()));

        return bootstrap;
    }

    @Bean(destroyMethod = "releaseExternalResources")
    public ServerBootstrap serverBootstrapElster() {
        final ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        final ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException,
            NoSuchProviderException {
                final ChannelPipeline pipeline = ApplicationContext.this.createPipeLine();
                LOGGER.info("Created new server pipeline");

                return pipeline;
            }
        });

        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", false);

        bootstrap.bind(new InetSocketAddress(this.oslpElsterPortServer()));

        return bootstrap;
    }

    private ChannelPipeline createPipeLine() throws NoSuchAlgorithmException, InvalidKeySpecException,
    NoSuchProviderException, IOException {
        final ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast("oslpEncoder", new OslpEncoder());
        pipeline.addLast("oslpDecoder", new OslpDecoder(this.oslpSignature(), this.oslpSignatureProvider()));
        pipeline.addLast("oslpSecurity", this.oslpSecurityHandler());

        pipeline.addLast("oslpChannelHandler", this.oslpChannelHandler());
        return pipeline;
    }

    @Bean
    public SimpleChannelHandler oslpSecurityHandler() {
        return new OslpSecurityHandler();
    }

    @Bean
    public OslpDecoder oslpDecoder() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException,
    NoSuchProviderException {
        return new OslpDecoder(this.oslpSignature(), this.oslpSignatureProvider());
    }

    @Bean
    public PublicKey publicKey() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException,
    NoSuchProviderException {
        return CertificateHelper.createPublicKey(
                this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_VERIFYKEY_PATH),
                this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_KEYTYPE),
                this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_PROVIDER));
    }

    @Bean
    public PrivateKey privateKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException,
    NoSuchProviderException {
        return CertificateHelper.createPrivateKey(
                this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_SIGNKEY_PATH),
                this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_KEYTYPE),
                this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_PROVIDER));
    }

    @Bean
    public String oslpSignatureProvider() {
        return this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_PROVIDER);
    }

    @Bean
    public String oslpSignature() {
        return this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_SIGNATURE);
    }

    @Bean
    public String oslpKeyType() {
        return this.environment.getProperty(PROPERTY_NAME_OSLP_SECURITY_KEYTYPE);
    }

    @Bean
    public int connectionTimeout() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_OSLP_TIMEOUT_CONNECT));
    }

    @Bean
    public int oslpPortClient() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_OSLP_PORT_CLIENT));
    }

    @Bean
    public int oslpElsterPortClient() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_OSLP_ELSTER_PORT_CLIENT));
    }

    @Bean
    public int oslpPortServer() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_OSLP_PORT_SERVER));
    }

    @Bean
    public int oslpElsterPortServer() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_OSLP_ELSTER_PORT_SERVER));
    }

    @Bean
    public String oslpAddressServer() {
        return this.environment.getProperty(PROPERTY_NAME_OSLP_ADDRESS_CLIENT);
    }

    @Bean
    public OslpChannelHandler oslpChannelHandler() {
        return new OslpChannelHandler();
    }

    @Bean
    public Integer sequenceNumberWindow() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_SEQUENCE_NUMBER_WINDOW));
    }

    @Bean
    public Integer sequenceNumberMaximum() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_SEQUENCE_NUMBER_MAXIMUM));
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
        return Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_CHECKBOX_DEVICE_REGISTRATION_VALUE));
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
        return Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_CHECKBOX_EVENT_NOTIFICATION_VALUE));
    }

    @PreDestroy
    public void destroyDataSource() {
        if (this.dataSource != null) {
            this.dataSource.close();
        }
    }
}
