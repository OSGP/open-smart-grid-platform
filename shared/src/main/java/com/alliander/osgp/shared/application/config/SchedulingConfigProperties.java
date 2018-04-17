/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.application.config;

import org.quartz.Job;

public class SchedulingConfigProperties {

    private final Class<? extends Job> jobClass;
    private final String threadCountKey;
    private final String cronExpressionKey;
    private final String jobStoreDbUrl;
    private final String jobStoreDbUsername;
    private final String jobStoreDbPassword;
    private final String jobStoreDbDriver;

    private SchedulingConfigProperties(final Builder builder) {
        this.jobClass = builder.jobClass;
        this.threadCountKey = builder.threadCountKey;
        this.cronExpressionKey = builder.cronExpressionKey;
        this.jobStoreDbUrl = builder.jobStoreDbUrl;
        this.jobStoreDbUsername = builder.jobStoreDbUsername;
        this.jobStoreDbPassword = builder.jobStoreDbPassword;
        this.jobStoreDbDriver = builder.jobStoreDbDriver;
    }

    public static class Builder {

        private Class<? extends Job> jobClass = null;
        private String threadCountKey = null;
        private String cronExpressionKey = null;
        private String jobStoreDbUrl = null;
        private String jobStoreDbUsername = null;
        private String jobStoreDbPassword = null;
        private String jobStoreDbDriver = null;

        public SchedulingConfigProperties build() {
            return new SchedulingConfigProperties(this);
        }

        public Builder withJobClass(final Class<? extends Job> jobClass) {
            this.jobClass = jobClass;
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
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Class<? extends Job> getJobClass() {
        return this.jobClass;
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

}
