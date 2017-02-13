/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.shared.application.config.AbstractConfig;

/**
 * Base class for the application configuration.
 */
@Configuration
@PropertySources({ @PropertySource("classpath:cucumber-platform.properties"),
        @PropertySource(value = "file:/etc/osp/test/global-cucumber.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:/etc/osp/test/cucumber-platform.properties", ignoreResourceNotFound = true), })
public class BaseApplicationConfiguration extends AbstractConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseApplicationConfiguration.class);

    @Value("${timeout}")
    private Integer timeout;

    @Value("${sleeptime}")
    private Integer sleeptime;

    /**
     * Gets the timeout. Either from the configuration (in war or
     * /etc/osp/test/global-cucumber.properties), or from the scenariocontext
     * (for a specific test).
     *
     * @return An integer representing the timeout.
     */
    public Integer getTimeout() {

        Integer retval = this.timeout;

        // For certain scenario's it is necessary to enlarge the timeout.
        if (ScenarioContext.Current().get(Keys.TIMEOUT) != null) {
            retval = Integer.parseInt(ScenarioContext.Current().get(Keys.TIMEOUT).toString());
        }

        LOGGER.debug("Using timeout [{}] seconds in the tests.", retval);

        return retval;
    }

    public Integer getSleepTime() {
        return this.sleeptime;
    }
}
