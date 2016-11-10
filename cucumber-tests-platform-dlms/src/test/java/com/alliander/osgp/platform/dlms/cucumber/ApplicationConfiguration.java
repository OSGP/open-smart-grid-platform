/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import com.alliander.osgp.shared.application.config.AbstractConfig;

/**
 * Base class for the application configuration.
 */
@Configuration
@PropertySources({ 
	@PropertySource("classpath:cucumber-platform-dlms.properties"),
	@PropertySource(value = "file:/etc/osp/test/global-cucumber.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "file:/etc/osp/test/cucumber-platform-dlms.properties", ignoreResourceNotFound = true),
})
public class ApplicationConfiguration extends AbstractConfig {
	
	@Value("${alarm.notifications.host}")
    public String alarmNotificationsHost;

	@Value("${alarm.notifications.port}")
    public String alarmNotificationsPort;

}
