package com.alliander.osgp.adapter.protocol.oslp.application.config;

import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.hibernate.ejb.HibernatePersistence;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.adapter.protocol.oslp.exceptions.ProtocolAdapterException;
import com.googlecode.flyway.core.Flyway;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@EnableJpaRepositories(entityManagerFactoryRef = "oslpEntityManagerFactory", basePackageClasses = { OslpDeviceRepository.class })
@Configuration
@EnableTransactionManagement()
@PropertySource("file:${osp/osgpAdapterProtocolOslp/config}")
public class OslpPersistenceConfig {

    private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
    private static final String PROPERTY_NAME_OSLP_DATABASE_URL = "db.url.oslp";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";

    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";

    private static final String PROPERTY_NAME_FLYWAY_INITIAL_VERSION = "flyway.initial.version";
    private static final String PROPERTY_NAME_FLYWAY_INITIAL_DESCRIPTION = "flyway.initial.description";
    private static final String PROPERTY_NAME_FLYWAY_INIT_ON_MIGRATE = "flyway.init.on.migrate";

    private static final String PROPERTY_NAME_OSLP_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan.oslp";

    private static final Logger LOGGER = LoggerFactory.getLogger(OslpPersistenceConfig.class);

    @Resource
    private Environment environment;

    public OslpPersistenceConfig() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    /**
     * Method for creating the Data Source.
     * 
     * @return DataSource
     */
    public DataSource oslpDataSource() {
        final SingleConnectionDataSource singleConnectionDataSource = new SingleConnectionDataSource();
        singleConnectionDataSource.setAutoCommit(false);
        final Properties properties = new Properties();
        properties.setProperty("socketTimeout", "0");
        properties.setProperty("tcpKeepAlive", "true");
        singleConnectionDataSource.setDriverClassName(this.environment
                .getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
        singleConnectionDataSource.setUrl(this.environment.getRequiredProperty(PROPERTY_NAME_OSLP_DATABASE_URL));
        singleConnectionDataSource.setUsername(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
        singleConnectionDataSource.setPassword(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));
        singleConnectionDataSource.setSuppressClose(true);
        return singleConnectionDataSource;
    }

    /**
     * Method for creating the Transaction Manager.
     * 
     * @return JpaTransactionManager
     * @throws ClassNotFoundException
     *             when class not found
     */
    @Bean
    public JpaTransactionManager transactionManager() throws ProtocolAdapterException {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();

        try {
            transactionManager.setEntityManagerFactory(this.oslpEntityManagerFactory().getObject());
            transactionManager.setTransactionSynchronization(JpaTransactionManager.SYNCHRONIZATION_ALWAYS);
        } catch (final ClassNotFoundException e) {
            final String msg = "Error in creating transaction manager bean";
            LOGGER.error(msg, e);
            throw new ProtocolAdapterException(msg, e);
        }

        return transactionManager;
    }

    /**
     * @return
     */
    @Bean(initMethod = "migrate")
    public Flyway oslpFlyway() {
        final Flyway flyway = new Flyway();

        // Initialization for non-empty schema with no metadata table
        flyway.setInitVersion(this.environment.getRequiredProperty(PROPERTY_NAME_FLYWAY_INITIAL_VERSION));
        flyway.setInitDescription(this.environment.getRequiredProperty(PROPERTY_NAME_FLYWAY_INITIAL_DESCRIPTION));
        flyway.setInitOnMigrate(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_FLYWAY_INIT_ON_MIGRATE)));

        flyway.setDataSource(this.oslpDataSource());

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
    @DependsOn("oslpFlyway")
    public LocalContainerEntityManagerFactoryBean oslpEntityManagerFactory() throws ClassNotFoundException {
        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setPersistenceUnitName("OSGP_PROTOCOL_ADAPTER_OSLP_SETTINGS");
        entityManagerFactoryBean.setDataSource(this.oslpDataSource());
        entityManagerFactoryBean.setPackagesToScan(this.environment
                .getRequiredProperty(PROPERTY_NAME_OSLP_ENTITYMANAGER_PACKAGES_TO_SCAN));
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
}
