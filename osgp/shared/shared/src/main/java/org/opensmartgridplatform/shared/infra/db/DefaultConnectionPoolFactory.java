/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Factory class which constructs a {@link HikariDataSource} instance. */
public class DefaultConnectionPoolFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConnectionPoolFactory.class);

  private String driverClassName;
  private String protocol;
  private String[] databaseHosts;
  private String databaseName;
  private String username;
  private String password;
  private int minPoolSize;
  private int maxPoolSize;
  private long initializationFailTimeout;
  private long validationTimeout;
  private long connectionTimeout;
  private boolean isAutoCommit;
  private int idleTimeout;
  private int maxLifetime;

  private DefaultConnectionPoolFactory() {
    // Private constructor to prevent instantiation of this class.
  }

  public String getDatabaseConnectionString() {
    final String result =
        String.format(
            "%s%s/%s", this.protocol, String.join(",", this.databaseHosts), this.databaseName);
    LOGGER.debug("Using database connection string '{}'", result);
    return result;
  }

  public HikariDataSource getDefaultConnectionPool() {
    final HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setDriverClassName(this.driverClassName);
    hikariConfig.setJdbcUrl(this.getDatabaseConnectionString());
    hikariConfig.setUsername(this.username);
    hikariConfig.setPassword(this.password);
    hikariConfig.setMinimumIdle(this.minPoolSize);
    hikariConfig.setMaximumPoolSize(this.maxPoolSize);
    hikariConfig.setInitializationFailTimeout(this.initializationFailTimeout);
    hikariConfig.setValidationTimeout(this.validationTimeout);
    hikariConfig.setConnectionTimeout(this.connectionTimeout);
    hikariConfig.setAutoCommit(this.isAutoCommit);
    hikariConfig.setIdleTimeout(this.idleTimeout);
    hikariConfig.setMaxLifetime(this.maxLifetime);
    return new HikariDataSource(hikariConfig);
  }

  /** Builder class which can construct an {@link DefaultConnectionPoolFactory} instance.} */
  public static class Builder {
    private String driverClassName = "org.postgresql.Driver";
    private String protocol = "jdbc:postgresql://";
    private String databaseHost = "localhost";
    private int databasePort = 5432;
    private String[] databaseHosts;
    private String databaseName = "";
    private String username = "";
    private String pw = "";
    private int minPoolSize = 1;
    private int maxPoolSize = 2;
    private long initializationFailTimeout = 1L;
    private long validationTimeout = 5000L;
    private long connectionTimeout = 30000L;
    private boolean isAutoCommit = false;
    private int idleTimeout = 120000;
    private int maxLifetime = 1800000;

    public Builder withDriverClassName(final String driverClassName) {
      this.driverClassName = driverClassName;
      return this;
    }

    public Builder withProtocol(final String protocol) {
      this.protocol = protocol;
      return this;
    }

    public Builder withDatabaseHost(final String databaseHost) {
      this.databaseHost = databaseHost;
      return this;
    }

    public Builder withDatabasePort(final int databasePort) {
      this.databasePort = databasePort;
      return this;
    }

    public Builder withDatabaseHosts(final String[] databaseHosts) {
      this.databaseHosts = databaseHosts;
      return this;
    }

    public Builder withDatabaseName(final String databaseName) {
      this.databaseName = databaseName;
      return this;
    }

    public Builder withUsername(final String username) {
      this.username = username;
      return this;
    }

    public Builder withPassword(final String password) {
      this.pw = password;
      return this;
    }

    public Builder withMinPoolSize(final int minPoolSize) {
      this.minPoolSize = minPoolSize;
      return this;
    }

    public Builder withMaxPoolSize(final int maxPoolSize) {
      this.maxPoolSize = maxPoolSize;
      return this;
    }

    public Builder withInitializationFailTimeout(final long initializationFailTimeout) {
      this.initializationFailTimeout = initializationFailTimeout;
      return this;
    }

    public Builder withValidationTimeout(final long validationTimeout) {
      this.validationTimeout = validationTimeout;
      return this;
    }

    public Builder withConnectionTimeout(final long connectionTimeout) {
      this.connectionTimeout = connectionTimeout;
      return this;
    }

    public Builder withAutoCommit(final boolean isAutoCommit) {
      this.isAutoCommit = isAutoCommit;
      return this;
    }

    public Builder withIdleTimeout(final int idleTimeout) {
      this.idleTimeout = idleTimeout;
      return this;
    }

    public Builder withMaxLifetime(final int maxLifetime) {
      this.maxLifetime = maxLifetime;
      return this;
    }

    public DefaultConnectionPoolFactory build() {
      final DefaultConnectionPoolFactory factory = new DefaultConnectionPoolFactory();
      factory.driverClassName = this.driverClassName;
      factory.protocol = this.protocol;
      if (this.databaseHosts != null && this.databaseHosts.length > 0) {
        factory.databaseHosts = this.databaseHosts;
      } else {
        factory.databaseHosts = new String[] {this.databaseHost + ":" + this.databasePort};
      }
      factory.databaseName = this.databaseName;
      factory.username = this.username;
      factory.password = this.pw;
      factory.minPoolSize = this.minPoolSize;
      factory.maxPoolSize = this.maxPoolSize;
      factory.initializationFailTimeout = this.initializationFailTimeout;
      factory.validationTimeout = this.validationTimeout;
      factory.connectionTimeout = this.connectionTimeout;
      factory.isAutoCommit = this.isAutoCommit;
      factory.idleTimeout = this.idleTimeout;
      factory.maxLifetime = this.maxLifetime;
      return factory;
    }
  }
}
