/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.core;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RetryableAssert {

    private RetryableAssert() {
        // Private constructor, utility class.
    }

    /**
     * Runs the given {@code assertion}, rescheduling it with the given
     * {@code delay} if an {@link AssertionError} occurs, for a maximum of
     * {@code numberOfRetries} times.
     */
    public static void assertWithRetries(final Runnable assertion, final int numberOfRetries, final long delay,
            final TimeUnit unit) {

        try {
            assertion.run();
        } catch (final AssertionError e) {
            if (numberOfRetries < 1) {
                throw e;
            }
            assertDelayedWithRetries(assertion, numberOfRetries - 1, delay, unit);
        }
    }

    /**
     * Runs the given {@code assertion} after an initial {@code delay},
     * rescheduling it with that same {@code delay} if an {@link AssertionError}
     * occurs, for a maximum of {@code numberOfRetries} times.
     */
    public static void assertDelayedWithRetries(final Runnable assertion, final int numberOfRetries, final long delay,
            final TimeUnit unit) {

        try {
            Executors.newSingleThreadScheduledExecutor()
                    .schedule(() -> assertWithRetries(assertion, numberOfRetries, delay, unit), delay, unit).get();
        } catch (final InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new CompletionException(ie);
        } catch (final ExecutionException ee) {
            if (ee.getCause() instanceof AssertionError) {
                throw (AssertionError) ee.getCause();
            } else {
                throw new CompletionException(ee.getCause());
            }
        }
    }
}
