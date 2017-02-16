/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.core.wait;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.cucumber.platform.config.PlatformApplicationConfiguration;

/**
 * Generic class with wait methods.
 */
@Component
public class Wait {

    /**
     * Platform configuration is needed for timeout settings.
     */
    private static PlatformApplicationConfiguration configuration;

    /**
     * Autowire the configuration.
     *
     * @param configuration
     *            The platform configuration.
     */
    @Autowired
    public Wait(final PlatformApplicationConfiguration configuration) {
        Wait.configuration = configuration;
    }

    /**
     * Wrap a collection of code and safely execute this in a timeout harness.
     *
     * @param <T>
     *            object of specific type
     * @param task
     *            bundle of code
     * @return object of a specific type T
     * @throws InterruptedException
     */
    public static <T> T ForResult(final Callable<T> task) throws InterruptedException {
        final Logger logger = LoggerFactory.getLogger(Wait.class);

        T response = null;
        boolean success = false;
        int count = 0;
        while (!success) {
            if (count / 1000 > configuration.getTimeout()) {
                Assert.fail("Timeout after [" + (count / 1000) + "] seconds");
            }
            logger.info("... polling in Wait.ForResult ...");

            try {
                // Call the code to run
                response = task.call();

                // If we get here set success to true, so make sure that we
                // stop.
                success = true;
                // We have a response, so exit
                // the while loop immediately
                break;
            } catch (final Exception ex) {
                handleException(logger, ex);
            }
            count += configuration.getSleepTime();
            TimeUnit.MILLISECONDS.sleep(configuration.getSleepTime());
        }
        return response;
    }

    /**
     * Logs the exception.
     *
     * @param logger
     * @param ex
     */
    public static void handleException(final Logger logger, final Exception ex) {
        logger.error("Caught an exception: [{}], stacktrace [{}]", ex.getMessage(), ex.getStackTrace());
    }
}
