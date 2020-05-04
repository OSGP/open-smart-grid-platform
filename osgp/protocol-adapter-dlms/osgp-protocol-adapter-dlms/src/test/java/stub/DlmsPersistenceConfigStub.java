/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package stub;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.shared.application.config.AbstractPersistenceConfig;
import org.opensmartgridplatform.shared.infra.db.DefaultConnectionPoolFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * An application context Java configuration class.
 */
@EnableJpaRepositories(entityManagerFactoryRef = "dlmsEntityManagerFactory", basePackageClasses = {
        DlmsDeviceRepository.class })
@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:osgp-adapter-protocol-dlms.properties")
public class DlmsPersistenceConfigStub extends AbstractPersistenceConfig {

    @Value("${db.username.dlms}")
    private String username;

    @Value("${db.password.dlms}")
    private String password;

    @Value("${db.host.dlms}")
    private String databaseHost;

    @Value("${db.port.dlms}")
    private int databasePort;

    @Value("${db.name.dlms}")
    private String databaseName;

    private DataSource dataSourceDlms;

    public DataSource getDataSourceDlms() {
        if (this.dataSourceDlms == null) {
            final DefaultConnectionPoolFactory.Builder builder = super.builder().withUsername(this.username)
                                                                      .withPassword(this.password)
                                                                      .withDatabaseHost(this.databaseHost)
                                                                      .withDatabasePort(this.databasePort)
                                                                      .withDatabaseName(this.databaseName);

            this.dataSourceDlms = new DataSource() {
                @Override
                public Connection getConnection() throws SQLException {
                    return null;
                }

                @Override
                public Connection getConnection(String username, String password) throws SQLException {
                    return null;
                }

                @Override
                public <T> T unwrap(Class<T> iface) throws SQLException {
                    return null;
                }

                @Override
                public boolean isWrapperFor(Class<?> iface) throws SQLException {
                    return false;
                }

                @Override
                public PrintWriter getLogWriter() throws SQLException {
                    return null;
                }

                @Override
                public void setLogWriter(PrintWriter out) throws SQLException {

                }

                @Override
                public void setLoginTimeout(int seconds) throws SQLException {

                }

                @Override
                public int getLoginTimeout() throws SQLException {
                    return 0;
                }

                @Override
                public Logger getParentLogger() throws SQLFeatureNotSupportedException {
                    return null;
                }
            };
        }
        return this.dataSourceDlms;
    }

    @Override
    @Bean
    public JpaTransactionManager transactionManager() {
        return super.transactionManager();
    }

    @Override
    @Bean(name = "dlmsEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        return super.entityManagerFactory("OSGP_PROTOCOL_ADAPTER_DLMS_SETTINGS", this.getDataSourceDlms());
    }

    @Override
    @PreDestroy
    public void destroyDataSource() {
        //
    }
}
