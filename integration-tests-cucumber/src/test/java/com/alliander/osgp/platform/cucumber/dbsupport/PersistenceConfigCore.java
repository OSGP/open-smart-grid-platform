/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.dbsupport;

import java.util.Properties;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.ejb.HibernatePersistence;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.domain.core.repositories.DeviceRepository;

//import com.alliander.osgp.adapter.ws.smartmetering.domain.repositories.MeterResponseDataRepository;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@EnableJpaRepositories(entityManagerFactoryRef = "entityMgrCore",  basePackageClasses = { DeviceRepository.class })
@Configuration
@EnableTransactionManagement()
@Primary
@PropertySource("file:/etc/osp/osgp-cucumber-response-data-smart-metering.properties")
public class PersistenceConfigCore {

    @Value("${cucumber.dbs.driver}")
    private String databaseDriver;

//    @Value("${cucumber.osgpadapterwssmartmeteringdbs.url}")
    @Value("${cucumber.osgpcoredbs.url}")
    private String databaseUrl;

    @Value("${cucumber.dbs.username}")
    private String databaseUsername;

    @Value("${cucumber.dbs.password}")
    private String databasePassword;

    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    @Value("${hibernate.dialect}")
    private String hibernateDialect;

    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    @Value("${hibernate.format_sql}")
    private String hibernateFormatSql;

    private static final String PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
    @Value("${hibernate.ejb.naming_strategy}")
    private String hibernateNamingStrategy;

    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    @Value("${hibernate.show_sql}")
    private String hibernateShowSql;

    @Value("${entitymanager.packages.to.scan.core}")
    private String entitymanagerPackagesToScan;

    @Resource
    private Environment environment;

  
    public PersistenceConfigCore() {
    }

    /**
     * Method for creating the Data Source.
     *
     * @return DataSource
     */
    @Primary
    @Bean(name = "dsCore")    
    public DataSource dataSource() {
        final SingleConnectionDataSource singleConnectionDataSource = new SingleConnectionDataSource();
        singleConnectionDataSource.setAutoCommit(false);
        final Properties properties = new Properties();
        properties.setProperty("socketTimeout", "0");
        properties.setProperty("tcpKeepAlive", "true");
        singleConnectionDataSource.setDriverClassName(this.databaseDriver);
        singleConnectionDataSource.setUrl(this.databaseUrl);
        singleConnectionDataSource.setUsername(this.databaseUsername);
        singleConnectionDataSource.setPassword(this.databasePassword);
        singleConnectionDataSource.setSuppressClose(true);
        return singleConnectionDataSource;
    }

    /**
     * Method for creating the Entity Manager Factory Bean.
     *
     * @return LocalContainerEntityManagerFactoryBean
     * @throws ClassNotFoundException
     *             when class not found
     */
    @Primary
    @Bean(name = "entityMgrCore")
    public LocalContainerEntityManagerFactoryBean entityMgrCore(@Qualifier("dsCore") DataSource dataSource)
            throws ClassNotFoundException {
        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setPersistenceUnitName("OSGP_CUCUMBER");
        entityManagerFactoryBean.setDataSource(this.dataSource());
        entityManagerFactoryBean.setPackagesToScan(this.entitymanagerPackagesToScan);
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistence.class);

        final Properties jpaProperties = new Properties();
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_DIALECT, this.hibernateDialect);
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_FORMAT_SQL, this.hibernateFormatSql);
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_NAMING_STRATEGY, this.hibernateNamingStrategy);
        jpaProperties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL, this.hibernateShowSql);

        entityManagerFactoryBean.setJpaProperties(jpaProperties);

        return entityManagerFactoryBean;
    }
    
    /**
     * Method for creating the Transaction Manager.
     *
     * @return JpaTransactionManager
     * @throws ClassNotFoundException
     *             when class not found
     */
    @Primary
    @Bean(name = "txMgrCore")    
    public JpaTransactionManager txMgrCore( 
            @Qualifier("entityMgrCore") EntityManagerFactory entityMgrCore) throws ClassNotFoundException {

        return new JpaTransactionManager(entityMgrCore);
    }

}
