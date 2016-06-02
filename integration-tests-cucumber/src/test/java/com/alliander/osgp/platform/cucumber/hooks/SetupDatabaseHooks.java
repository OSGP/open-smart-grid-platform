/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.hooks;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

//@Component
@Configuration
@PropertySource("file:/etc/osp/osgp-cucumber-response-data-smart-metering.properties")
public class SetupDatabaseHooks {

    // @Before()
    public void deleteDevideLogItem() {

    }

    // private final Logger LOGGER =
    // LoggerFactory.getLogger(SetupDatabaseHooks.class);
    //
    // private Connection connection;
    //
    // @Value("${cucumber.readdbs.url}")
    // private String jdbcUrl;
    //
    // @Value("${cucumber.polldbs.username}")
    // private String username;
    //
    // @Value("${cucumber.polldbs.password}")
    // private String password;
    //
    // private void closeStatement(final Statement statement) {
    // if (statement != null) {
    // try {
    // statement.close();
    // } catch (final SQLException e) {
    // this.LOGGER.error(e.getMessage());
    // }
    // }
    // }
    //
    // private void closeResultSet(final ResultSet rs) {
    // try {
    // rs.close();
    // } catch (final SQLException e) {
    // this.LOGGER.error(e.getMessage());
    // }
    // }
    //
    // private Connection conn() {
    // if (this.connection == null) {
    // this.connection = this.connectToDatabaseOrDie();
    // }
    // return this.connection;
    // }
    //
    // private Connection connectToDatabaseOrDie() {
    // try {
    // Class.forName("org.postgresql.Driver");
    // this.connection = DriverManager.getConnection(this.jdbcUrl,
    // this.username, this.password);
    // } catch (final ClassNotFoundException e) {
    // this.LOGGER.error(e.getMessage());
    // ;
    // System.exit(1);
    // } catch (final SQLException e) {
    // this.LOGGER.error(e.getMessage());
    // ;
    // System.exit(2);
    // }
    // return this.connection;
    // }
    //
    // @Before
    // public void deleteDevideLogItem() {
    // Statement statement = null;
    // try {
    // statement = this.conn().createStatement();
    // ResultSet rs = null;
    //
    // try {
    // rs = statement.executeQuery("DELETE device_log_item");
    // final ArrayList<String> result = new ArrayList<String>();
    //
    // while (rs.next()) {
    // result.add(rs.getString(1));
    // result.add(rs.getString(2));
    // }
    //
    // // return true;
    // } catch (final SQLException se) {
    // this.LOGGER.error(se.getMessage());
    // // return false;
    // } finally {
    // this.closeResultSet(rs);
    // }
    //
    // } catch (final SQLException se) {
    // this.LOGGER.error(se.getMessage());
    // // return false;
    // } finally {
    // this.closeStatement(statement);
    // }
    // }
}
