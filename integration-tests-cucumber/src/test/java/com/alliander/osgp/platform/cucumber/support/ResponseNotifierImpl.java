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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

@Component
@Configuration
@PropertySource("file:/etc/osp/osgp-cucumber-response-data-smart-metering.properties")
public class ResponseNotifierImpl implements ResponseNotifier {

    private final Logger LOGGER = LoggerFactory.getLogger(ResponseNotifierImpl.class);

    private Connection connection;

    @Value("${cucumber.polldbs.url}")
    private String jdbcUrl;

    @Value("${cucumber.polldbs.username}")
    private String username;

    @Value("${cucumber.polldbs.password}")
    private String password;

    public boolean waitForResponse(final String correlid, final int laptime, final int maxlaps) {
        Statement statement = null;
        try {
            statement = conn().createStatement();
            int pollcount = 0;

            while (true) {
                Thread.sleep(laptime);
                if (pollcount++ < maxlaps) {
                    PollResult pollres = pollDatabase(statement, correlid);
                    if (pollres.equals(PollResult.OK)) {
                        return true;
                    } else if (pollres.equals(PollResult.ERROR)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (SQLException se) {
            LOGGER.error(se.getMessage());
            return false;
        } catch(InterruptedException intex) {
            LOGGER.error(intex.getMessage());
            return false;
        } finally {
            closeStatement(statement);
        }
    }

    private PollResult pollDatabase(final Statement statement, final String correlid) {
        ResultSet rs =  null;
        PollResult result = PollResult.NOT_OK;
        try {
            rs = statement.executeQuery("SELECT count(*) FROM meter_response_data WHERE correlation_uid = '" + correlid + "'");
            while (rs.next()) {
                if (rs.getInt(1) > 0) {
                    result = PollResult.OK;
                }
            }
            rs.close();
            return result;
        } catch (SQLException se) {
            LOGGER.error(se.getMessage());
            return PollResult.ERROR;
        } finally {
            closeResultSet(rs);
        }
    }
    
    private void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }


    private void closeResultSet(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
    
    private Connection conn() {
        if (connection == null) {
            connection = connectToDatabaseOrDie();
        }
        return connection;
    }

    private Connection connectToDatabaseOrDie() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());;
            System.exit(1);
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());;
            System.exit(2);
        }
        return connection;
    }

   
    //-------------
    
  //To resolve ${} in @Value
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    private enum PollResult {
        OK,
        NOT_OK,
        ERROR;
    }
}
