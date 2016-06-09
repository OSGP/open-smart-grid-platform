/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Configuration
@PropertySource("file:/etc/osp/osgp-cucumber-response-data-smart-metering.properties")
public class DatabaseReader {

    private final Logger LOGGER = LoggerFactory.getLogger(DatabaseReader.class);

    private Connection connection;

    @Value("${cucumber.osgploggingdbs.url}")
    private String jdbcUrl;

    @Value("${cucumber.dbs.username}")
    private String username;

    @Value("${cucumber.dbs.password}")
    private String password;

    private void closeStatement(final Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (final SQLException e) {
                this.LOGGER.error(e.getMessage());
            }
        }
    }

    private void closeResultSet(final ResultSet rs) {
        try {
            rs.close();
        } catch (final SQLException e) {
            this.LOGGER.error(e.getMessage());
        }
    }

    private Connection conn() {
        if (this.connection == null) {
            this.connection = this.connectToDatabaseOrDie();
        }
        return this.connection;
    }

    private Connection connectToDatabaseOrDie() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(this.jdbcUrl, this.username, this.password);
        } catch (final ClassNotFoundException e) {
            this.LOGGER.error(e.getMessage());
            ;
            System.exit(1);
        } catch (final SQLException e) {
            this.LOGGER.error(e.getMessage());
            ;
            System.exit(2);
        }
        return this.connection;
    }

    public List<String> readDevideLogItem(final String table, final String knownDevice, final String unknownDevice) {
        Statement statement = null;
        try {
            statement = this.conn().createStatement();
            ResultSet rs = null;

            try {
                rs = statement
                        .executeQuery("SELECT creation_time, decoded_message FROM device_log_item WHERE device_identification IN ('"
                                + knownDevice + "'" + " ,'" + unknownDevice + "') ORDER BY CREATION_TIME DESC LIMIT 2");
                final ArrayList<String> result = new ArrayList<String>();

                while (rs.next()) {
                    result.add(rs.getString(1));
                    result.add(rs.getString(2));
                }

                return result;
            } catch (final SQLException se) {
                this.LOGGER.error(se.getMessage());
                return null;
            } finally {
                this.closeResultSet(rs);
            }

        } catch (final SQLException se) {
            this.LOGGER.error(se.getMessage());
            return null;
        } finally {
            this.closeStatement(statement);
        }
    }
}
