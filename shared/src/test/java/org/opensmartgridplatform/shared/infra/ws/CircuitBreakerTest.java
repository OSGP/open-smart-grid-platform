/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.ws;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CircuitBreakerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerTest.class);

    private CircuitBreaker circuitBreaker;

    @Before
    public void before() {
        this.circuitBreaker = new CircuitBreaker.Builder().withThreshold(2).withInitialDuration(30)
                .withMaximumDuration(120).withMultiplier(3).build();
    }

    @Test
    public void testNegativeThreshold() {
        LOGGER.info("Test: expect using a negative threshold fails");
        try {
            new CircuitBreaker.Builder().withThreshold(-1).build();
            this.failIllegalArgument("threshold");
        } catch (final IllegalArgumentException e) {
            this.checkIllegalArgumentExceptionMessage(e);
        }
    }

    @Test
    public void testNegativeInitialDuration() {
        LOGGER.info("Test: expect using a negative initial duration fails");
        try {
            new CircuitBreaker.Builder().withInitialDuration(-1).build();
            this.failIllegalArgument("initialDuration");
        } catch (final IllegalArgumentException e) {
            this.checkIllegalArgumentExceptionMessage(e);
        }
    }

    @Test
    public void testNegativeMaximumDuration() {
        LOGGER.info("Test: expect using a negative maximum duration fails");
        try {
            new CircuitBreaker.Builder().withMaximumDuration(-1).build();
            this.failIllegalArgument("maximumDuration");
        } catch (final IllegalArgumentException e) {
            this.checkIllegalArgumentExceptionMessage(e);
        }
    }

    @Test
    public void testNegativeMultiplier() {
        LOGGER.info("Test: expect using a negative multiplier fails");
        try {
            new CircuitBreaker.Builder().withMultiplier(-1).build();
            this.failIllegalArgument("multiplier");
        } catch (final IllegalArgumentException e) {
            this.checkIllegalArgumentExceptionMessage(e);
        }
    }

    @Test
    public void testZeroValues() {
        LOGGER.info("Test: expect using zero values for all parameters is allowed");
        try {
            new CircuitBreaker.Builder().withThreshold(0).withInitialDuration(0).withMaximumDuration(0)
                    .withMultiplier(0).build();
        } catch (final Exception e) {
            Assert.fail("Exception should not occur for valid build call");
        }
    }

    private void failIllegalArgument(final String fieldName) {
        Assert.fail(String.format("CircuitBreaker should not have been created because of an illegal value for [%s]",
                fieldName));
    }

    private void checkIllegalArgumentExceptionMessage(final IllegalArgumentException e) {
        // Checks an essential part of the error message instead
        // of the exact full text.
        final String exceptionMessage = e.getMessage();
        Assert.assertTrue(exceptionMessage.contains("negative value"));
    }

    @Test
    public void testInitiallyClosed() {
        LOGGER.info("Test: expect initial status is CLOSED");
        Assert.assertTrue("Initial status should be CLOSED", this.circuitBreaker.isClosed());
    }

    @Test
    public void testOpenCircuit() {
        LOGGER.info("Test: expect status is OPEN after an explicit open");
        this.circuitBreaker.openCircuit();
        Assert.assertFalse("Should be OPEN after explicit open circuit request", this.circuitBreaker.isClosed());
    }

    @Test
    public void testCloseCircuit() {
        LOGGER.info("Test: expect status is OPEN after an explicit close");
        this.markTwoFailures();
        this.circuitBreaker.closeCircuit();
        Assert.assertTrue("Should be CLOSED after explicit close circuit request", this.circuitBreaker.isClosed());
    }

    @Test
    public void testClosedAfter1Failure() {
        LOGGER.info("Test: expect status is CLOSED after one failure");
        this.markFailure();
        Assert.assertTrue("Status should be CLOSED after 1 failure", this.circuitBreaker.isClosed());
    }

    @Test
    public void testOpenAfter2Failures() {
        LOGGER.info("Test: expect status is OPEN after two consecutive failures");
        this.markTwoFailures();

        Assert.assertFalse("Status should be OPEN after 2 failures", this.circuitBreaker.isClosed());
    }

    @Test
    public void testClosedAfter2FailuresAndWait() {
        LOGGER.info("Test: expect status is CLOSED after two failures and waiting longer than the initial duration");
        // Trigger the circuit breaker to open
        this.markTwoFailures();
        // Wait until the circuit breaker is closed
        this.wait(35);

        Assert.assertTrue("Status should be CLOSED after waiting for 35 ms", this.circuitBreaker.isClosed());
    }

    @Test
    public void testDurationIncrease() {
        LOGGER.info("Test: expect status is OPEN after waiting shorter than the current duration of 600 ms");
        this.circuitBreaker = new CircuitBreaker.Builder().withThreshold(2).withInitialDuration(30)
                .withMaximumDuration(1200).withMultiplier(20).build();

        // Trigger the circuit breaker to open
        this.markTwoFailures();
        // Wait until the circuit breaker is closed
        this.wait(35);

        // We are in a half open state now. Another failure results
        // in increasing the duration from 30 to 600 milliseconds.
        this.markFailure();
        // Wait longer then the initial duration, but shorter than
        // multiplier * initial duration.
        this.wait(100);

        Assert.assertFalse("Status should be OPEN after waiting for 100 ms", this.circuitBreaker.isClosed());
    }

    @Test
    public void testMaximumDuration() {
        // Trigger the circuit breaker to open
        this.markTwoFailures();
        // Wait until the circuit breaker is closed
        this.wait(35);

        // We are in a half open state now. Another failure results
        // in tripling the timeout from 30 to 90 milliseconds.
        this.markFailure();
        // Wait until the circuit breaker is closed
        this.wait(95);

        // We are in a half open state now. Another failure results
        // in increasing the duration from 90 to its maximum of 120
        // milliseconds.
        this.markFailure();

        // Wait until the circuit breaker is closed
        this.wait(125);

        Assert.assertTrue("Status should be CLOSED after waiting for 125 ms", this.circuitBreaker.isClosed());
    }

    @Test
    public void testClosedAfterFailingOnceAfterSuccess() {
        this.markFailure();
        // One success resets the threshold to 2.
        this.markSuccess();
        this.markFailure();

        Assert.assertTrue("Status should be CLOSED, when there's one failure after a success",
                this.circuitBreaker.isClosed());
    }

    private void markFailure() {
        this.circuitBreaker.markFailure();
    }

    private void markTwoFailures() {
        this.markFailure();
        this.markFailure();
    }

    private void markSuccess() {
        this.circuitBreaker.markSuccess();
    }

    private void wait(final int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (final InterruptedException e) {
            Assert.fail("Sleep interrupted");
        }
    }
}
