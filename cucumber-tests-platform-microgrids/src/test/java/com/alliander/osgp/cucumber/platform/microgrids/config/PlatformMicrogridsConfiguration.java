/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.microgrids.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import com.alliander.osgp.cucumber.platform.config.AbstractPlatformApplicationConfiguration;

@Configuration
@PropertySources({ @PropertySource("classpath:cucumber-tests-platform-microgrids.properties"),
        @PropertySource(value = "file:/etc/osp/test/global-cucumber.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:/etc/osp/test/cucumber-tests-platform-microgrids.properties", ignoreResourceNotFound = true), })
public class PlatformMicrogridsConfiguration extends AbstractPlatformApplicationConfiguration {

    @Value("${jaxb2.marshaller.context.path.microgrids.notification}")
    private String contextPathMicrogridsNotification;

    @Value("${web.service.notification.context}")
    private String notificationsContextPath;

    @Value("${web.service.notification.port}")
    private int notificationsPort;

    public String getContextPathMicrogridsNotification() {
        return this.contextPathMicrogridsNotification;
    }

    public String getNotificationsContextPath() {
        return this.notificationsContextPath;
    }

    public int getNotificationsPort() {
        return this.notificationsPort;
    }
}
