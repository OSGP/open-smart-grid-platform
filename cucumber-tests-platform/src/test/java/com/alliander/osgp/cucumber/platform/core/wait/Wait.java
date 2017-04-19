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
    public static <T> T until(final Callable<T> task) {
        final Logger logger = LoggerFactory.getLogger(Wait.class);

        T response = null;
        boolean success = false;
        int count = 0;
        while (!success) {
            if (count / 1000 > configuration.getTimeout()) {
                Assert.fail("Timeout after [" + (count / 1000) + "] seconds");
            }
            if (count > 0) {
                logger.info("... polling in Wait.until (" + (count / 1000) + " seconds)...");
            }

            try {
                // Call the code to run
                response = task.call();

                // If we get here set success to true, so make sure that we
                // stop.
                success = true;
                // We have a response, so exit
                // the while loop immediately
                break;
            } catch (final AssertionError error) {
                handleAssertionError(logger, error);
            } catch (final Exception ex) {
                handleException(logger, ex);
            }
            count += configuration.getSleepTime();
            try {
                TimeUnit.MILLISECONDS.sleep(configuration.getSleepTime());
            } catch (final Exception ex) {
                handleException(logger, ex);
            }
        }
        return response;
    }

    public static <T> void until(final Runnable task) {
        final Logger logger = LoggerFactory.getLogger(Wait.class);

        boolean success = false;
        int count = 0;
        while (!success) {
            if (count / 1000 > configuration.getTimeout()) {
                Assert.fail("Timeout after [" + (count / 1000) + "] seconds.");
            }
            if (count > 0) {
                logger.info("... polling in Wait.until (" + (count / 1000) + " seconds) ...");
            }

            try {
                // Call the code to run
                task.run();

                // If we get here set success to true, so make sure that we
                // stop.
                success = true;
                // We have a response, so exit
                // the while loop immediately
                break;
            } catch (final AssertionError error) {
                handleAssertionError(logger, error);
            } catch (final Exception ex) {
                handleException(logger, ex);
            }
            count += configuration.getSleepTime();
            try {
                TimeUnit.MILLISECONDS.sleep(configuration.getSleepTime());
            } catch (final Exception ex) {
                handleException(logger, ex);
            }
        }
    }

    private static void handleAssertionError(final Logger logger, final AssertionError error) {
        logger.error("Assertion error: " + error.getMessage());
        logger.debug("               : stacktrace [" + error.getStackTrace() + "]");
    }

    /**
     * Logs the exception.
     *
     * @param logger
     * @param ex
     */
    private static void handleException(final Logger logger, final Exception ex) {
        logger.error("Caught an exception: [{}]", ex.getMessage());
        logger.debug("                   : stacktrace [" + ex.getStackTrace() + "]");
    }
}
