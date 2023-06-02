//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.application.config;

import org.quartz.Job;

public class SchedulingConfigProperties {

  private static final String DEFAULT_MAX_CONNECTIONS = "5";

  private final Class<? extends Job> jobClass;
  private final String schedulerName;
  private final String threadCountKey;
  private final String cronExpressionKey;
  private final String jobStoreDbUrl;
  private final String jobStoreDbUsername;
  private final String jobStoreDbPassword;
  private final String jobStoreDbDriver;
  private final String maxConnections;
  private final boolean useProperties;

  private SchedulingConfigProperties(final Builder builder) {
    this.jobClass = builder.jobClass;
    this.schedulerName = builder.schedulerName;
    this.threadCountKey = builder.threadCountKey;
    this.cronExpressionKey = builder.cronExpressionKey;
    this.jobStoreDbUrl = builder.jobStoreDbUrl;
    this.jobStoreDbUsername = builder.jobStoreDbUsername;
    this.jobStoreDbPassword = builder.jobStoreDbPassword;
    this.jobStoreDbDriver = builder.jobStoreDbDriver;
    this.maxConnections = builder.maxConnections;
    this.useProperties = builder.useProperties;
  }

  public static class Builder {

    private Class<? extends Job> jobClass = null;
    private String schedulerName = null;
    private String threadCountKey = null;
    private String cronExpressionKey = null;
    private String jobStoreDbUrl = null;
    private String jobStoreDbUsername = null;
    private String jobStoreDbPassword = null;
    private String jobStoreDbDriver = null;
    private String maxConnections = DEFAULT_MAX_CONNECTIONS;
    private boolean useProperties = true;

    public SchedulingConfigProperties build() {
      return new SchedulingConfigProperties(this);
    }

    public Builder withJobClass(final Class<? extends Job> jobClass) {
      this.jobClass = jobClass;
      return this;
    }

    public Builder withSchedulerName(final String schedulerName) {
      this.schedulerName = schedulerName;
      return this;
    }

    public Builder withThreadCountKey(final String threadCountKey) {
      this.threadCountKey = threadCountKey;
      return this;
    }

    public Builder withCronExpressionKey(final String cronExpressionKey) {
      this.cronExpressionKey = cronExpressionKey;
      return this;
    }

    public Builder withJobStoreDbUrl(final String jobStoreDbUrl) {
      this.jobStoreDbUrl = jobStoreDbUrl;
      return this;
    }

    public Builder withJobStoreDbUsername(final String jobStoreDbUsername) {
      this.jobStoreDbUsername = jobStoreDbUsername;
      return this;
    }

    public Builder withJobStoreDbPassword(final String jobStoreDbPassword) {
      this.jobStoreDbPassword = jobStoreDbPassword;
      return this;
    }

    public Builder withJobStoreDbDriver(final String jobStoreDbDriver) {
      this.jobStoreDbDriver = jobStoreDbDriver;
      return this;
    }

    public Builder withMaxConnections(final String maxConnections) {
      this.maxConnections = maxConnections;
      return this;
    }

    public Builder withUseProperties(final boolean useProperties) {
      this.useProperties = useProperties;
      return this;
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public Class<? extends Job> getJobClass() {
    return this.jobClass;
  }

  public String getSchedulerName() {
    return this.schedulerName;
  }

  public String getThreadCountKey() {
    return this.threadCountKey;
  }

  public String getCronExpressionKey() {
    return this.cronExpressionKey;
  }

  public String getJobStoreDbUrl() {
    return this.jobStoreDbUrl;
  }

  public String getJobStoreDbUsername() {
    return this.jobStoreDbUsername;
  }

  public String getJobStoreDbPassword() {
    return this.jobStoreDbPassword;
  }

  public String getJobStoreDbDriver() {
    return this.jobStoreDbDriver;
  }

  public String getMaxConnections() {
    return this.maxConnections;
  }

  public boolean isUseProperties() {
    return this.useProperties;
  }
}
