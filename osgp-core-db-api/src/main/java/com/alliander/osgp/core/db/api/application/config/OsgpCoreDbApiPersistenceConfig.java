package com.alliander.osgp.core.db.api.application.config;

import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.hibernate.ejb.HibernatePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.core.db.api.exceptions.CoreDbApiException;
import com.alliander.osgp.core.db.api.repositories.DeviceDataRepository;

@EnableJpaRepositories(entityManagerFactoryRef = "osgpCoreDbApiEntityManagerFactory", basePackageClasses = { DeviceDataRepository.class })
@Configuration
@EnableTransactionManagement()
@PropertySource("file:${osp/osgpCoreDbApi/config}")
public class OsgpCoreDbApiPersistenceConfig {

    private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.api.driver";
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.api.password";
    private static final String PROPERTY_NAME_DATABASE_URL = "db.api.url";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.api.username";

    private static final String HIBERNATE_DIALECT_KEY = "hibernate.dialect";
    private static final String HIBERNATE_FORMAT_SQL_KEY = "hibernate.format_sql";
    private static final String HIBERNATE_NAMING_STRATEGY_KEY = "hibernate.ejb.naming_strategy";
    private static final String HIBERNATE_SHOW_SQL_KEY = "hibernate.show_sql";

    private static final String PROPERTY_NAME_HIBERNATE_DIALECT_VALUE = "api.hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL_VALUE = "api.hibernate.format_sql";
    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY_VALUE = "api.hibernate.ejb.naming_strategy";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL_VALUE = "api.hibernate.show_sql";

    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "api.entitymanager.packages.to.scan";

    private static final Logger LOGGER = LoggerFactory.getLogger(OsgpCoreDbApiPersistenceConfig.class);

    @Resource
    private Environment environment;

    /**
     * Method for creating the Data Source.
     * 
     * @return DataSource
     */
    public DataSource osgpCoreDbApiDataSource() {
        final SingleConnectionDataSource singleConnectionDataSource = new SingleConnectionDataSource();
        singleConnectionDataSource.setAutoCommit(false);
        final Properties properties = new Properties();
        properties.setProperty("socketTimeout", "0");
        properties.setProperty("tcpKeepAlive", "true");
        singleConnectionDataSource.setConnectionProperties(properties);
        singleConnectionDataSource.setDriverClassName(this.environment
                .getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
        singleConnectionDataSource.setUrl(this.environment.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
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
    public JpaTransactionManager osgpCoreDbApiTransactionManager() throws CoreDbApiException {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();

        try {
            transactionManager.setEntityManagerFactory(this.osgpCoreDbApiEntityManagerFactory().getObject());
            transactionManager.setTransactionSynchronization(JpaTransactionManager.SYNCHRONIZATION_ALWAYS);
        } catch (final ClassNotFoundException e) {
            final String msg = "Error in creating transaction manager bean";
            LOGGER.error(msg, e);
            throw new CoreDbApiException(msg, e);
        }

        return transactionManager;
    }

    /**
     * Method for creating the Entity Manager Factory Bean.
     * 
     * @return LocalContainerEntityManagerFactoryBean
     * @throws ClassNotFoundException
     *             when class not found
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean osgpCoreDbApiEntityManagerFactory() throws ClassNotFoundException {
        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setPersistenceUnitName("OSGP_CORE_DB_API");
        entityManagerFactoryBean.setDataSource(this.osgpCoreDbApiDataSource());
        entityManagerFactoryBean.setPackagesToScan(this.environment
                .getRequiredProperty(PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN));
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistence.class);

        final Properties jpaProperties = new Properties();
        jpaProperties.put(HIBERNATE_DIALECT_KEY,
                this.environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT_VALUE));
        jpaProperties.put(HIBERNATE_FORMAT_SQL_KEY,
                this.environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL_VALUE));
        jpaProperties.put(HIBERNATE_NAMING_STRATEGY_KEY,
                this.environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY_VALUE));
        jpaProperties.put(HIBERNATE_SHOW_SQL_KEY,
                this.environment.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL_VALUE));

        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }
}
