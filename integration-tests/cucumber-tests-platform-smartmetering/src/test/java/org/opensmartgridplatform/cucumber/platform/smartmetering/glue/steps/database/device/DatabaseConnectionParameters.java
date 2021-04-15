/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.device;

public class DatabaseConnectionParameters {
  public static String getDriver() {
    return System.getenv("db_driver") != null
        ? System.getenv("db_driver")
        : "org.postgresql.Driver";
  }

  public static String getHost() {
    return System.getenv("db_host") != null ? System.getenv("db_host") : "localhost";
  }

  public static String getPort() {
    return System.getenv("db_port") != null ? System.getenv("db_port") : "5432";
  }

  public static String getDatabase() {
    return System.getenv("db_database") != null
        ? System.getenv("db_database")
        : "osgp_secret_management";
  }

  public static String getUser() {
    return System.getenv("db_user") != null ? System.getenv("db_user") : "osp_admin";
  }

  public static String getPassword() {
    return System.getenv("db_password") != null ? System.getenv("db_password") : "1234";
  }
}
