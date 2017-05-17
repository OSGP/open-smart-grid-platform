/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Base class for the application configuration.
 */
@Configuration
@PropertySources({ @PropertySource("classpath:cucumber-tests-core.properties"),
        @PropertySource(value = "file:/etc/osp/test/global-cucumber.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:/etc/osp/test/cucumber-tests-core.properties", ignoreResourceNotFound = true), })
public class CoreApplicationConfiguration extends BaseApplicationConfiguration {

}
