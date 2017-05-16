/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.alliander.osgp.cucumber.core.Keys;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.shared.application.config.AbstractConfig;

/**
 * Base class for the application configuration.
 */
public abstract class BaseApplicationConfiguration extends AbstractConfig {

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
        if (ScenarioContext.current().get(Keys.TIMEOUT) != null) {
            retval = Integer.parseInt(ScenarioContext.current().get(Keys.TIMEOUT).toString());
        }

        LOGGER.debug("Using timeout [{}] seconds in the tests.", retval);

        return retval;
    }

    public Integer getSleepTime() {
        return this.sleeptime;
    }
}
