/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.publiclighting.application.config;

import javax.annotation.PostConstruct;

import org.opensmartgridplatform.adapter.ws.shared.services.ResendNotificationJob;
import org.opensmartgridplatform.shared.application.scheduling.OsgpScheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-publiclighting.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsPublicLighting/config}", ignoreResourceNotFound = true)
public class ResendNotificationScheduledJobConfig {

    @Value("${publiclighting.scheduling.job.resend.notification.cron.expression}")
    private String cronExpressionResendNotification;

    @Value("${publiclighting.scheduling.job.resend.notification.maximum:2}")
    private short resendNotificationMaximum;

    @Value("${publiclighting.scheduling.job.resend.notification.multiplier:2}")
    private int resendNotificationMultiplier;

    @Value("${publiclighting.scheduling.job.resend.notification.resend.threshold.in.minutes:1}")
    private int resendThresholdInMinutes;

    @Value("${publiclighting.scheduling.job.resend.notification.page.size:1}")
    private int resendPageSize;

    @Autowired
    private OsgpScheduler osgpScheduler;

    @Bean
    public short resendNotificationMaximum() {
        return this.resendNotificationMaximum;
    }

    @Bean
    public int resendNotificationMultiplier() {
        return this.resendNotificationMultiplier;
    }

    @Bean
    public int resendThresholdInMinutes() {
        return this.resendThresholdInMinutes;
    }

    @Bean
    public int resendPageSize() {
        return this.resendPageSize;
    }

    @PostConstruct
    private void initializeScheduledJob() throws SchedulerException {
        this.osgpScheduler.createAndScheduleJob(ResendNotificationJob.class, this.cronExpressionResendNotification);
    }
}
