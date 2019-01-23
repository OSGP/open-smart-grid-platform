/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.shared.services;

import org.opensmartgridplatform.shared.application.config.AbstractSchedulingConfig;
import org.opensmartgridplatform.shared.application.config.SchedulingConfigProperties.Builder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Value;

/**
 * Base class for ResponseDataCleanupConfig classes for components of OSGP.
 */
public abstract class AbstractResponseDataCleanupSchedulingConfig extends AbstractSchedulingConfig {

    @Value("${db.driver}")
    protected String databaseDriver;

    @Value("${db.password}")
    protected String databasePassword;

    @Value("${db.protocol}")
    protected String databaseProtocol;

    @Value("${db.host}")
    protected String databaseHost;

    @Value("${db.port}")
    protected String databasePort;

    @Value("${db.name}")
    protected String databaseName;

    @Value("${db.username}")
    protected String databaseUsername;

    public String getDatabaseUrl() {
        return this.databaseProtocol + this.databaseHost + ":" + this.databasePort + "/" + this.databaseName;
    }

    public abstract int cleanupJobRetentionTimeInDays();

    public abstract Scheduler cleanupResponseDataScheduler() throws SchedulerException;

    protected abstract Builder abstractSchedulingConfigBuilder();

}
