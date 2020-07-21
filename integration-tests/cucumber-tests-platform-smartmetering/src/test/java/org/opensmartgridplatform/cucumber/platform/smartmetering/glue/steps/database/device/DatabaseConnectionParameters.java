package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.device;

public class DatabaseConnectionParameters {
    public String getDriver() {
        return System.getenv("db_driver") != null ? System.getenv("db_driver") : "org.postgresql.Driver";
    }

    public String getHost() {
        return System.getenv("db_host") != null ? System.getenv("db_host") : "localhost";
    }

    public String getPort() {
        return System.getenv("db_port") != null ? System.getenv("db_port") : "5432";
    }

    public String getDatabase() {
        return System.getenv("db_database") != null ? System.getenv("db_database") : "secret-management";
    }

    public String getUser() {
        return System.getenv("db_user") != null ? System.getenv("db_user") : "osp_admin";
    }

    public String getPassword() {
        return System.getenv("db_password") != null ? System.getenv("db_password") : "1234";
    }
}
