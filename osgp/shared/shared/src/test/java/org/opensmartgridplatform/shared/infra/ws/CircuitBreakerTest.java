// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.infra.ws;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CircuitBreakerTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerTest.class);

  private CircuitBreaker circuitBreaker;

  @BeforeEach
  public void before() {
    this.circuitBreaker =
        new CircuitBreaker.Builder()
            .withThreshold(2)
            .withInitialDuration(30)
            .withMaximumDuration(120)
            .withMultiplier(3)
            .build();
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
      new CircuitBreaker.Builder()
          .withThreshold(0)
          .withInitialDuration(0)
          .withMaximumDuration(0)
          .withMultiplier(0)
          .build();
    } catch (final Exception e) {
      fail("Exception should not occur for valid build call");
    }
  }

  private void failIllegalArgument(final String fieldName) {
    fail(
        String.format(
            "CircuitBreaker should not have been created because of an illegal value for [%s]",
            fieldName));
  }

  private void checkIllegalArgumentExceptionMessage(final IllegalArgumentException e) {
    // Checks an essential part of the error message instead
    // of the exact full text.
    final String exceptionMessage = e.getMessage();
    assertThat(exceptionMessage.contains("negative value")).isTrue();
  }

  @Test
  public void testInitiallyClosed() {
    LOGGER.info("Test: expect initial status is CLOSED");
    assertThat(this.circuitBreaker.isClosed())
        .withFailMessage("Initial status should be CLOSED")
        .isTrue();
  }

  @Test
  public void testOpenCircuit() {
    LOGGER.info("Test: expect status is OPEN after an explicit open");
    this.circuitBreaker.openCircuit();
    assertThat(this.circuitBreaker.isClosed())
        .withFailMessage("Should be OPEN after explicit open circuit request")
        .isFalse();
  }

  @Test
  public void testCloseCircuit() {
    LOGGER.info("Test: expect status is OPEN after an explicit close");
    this.markTwoFailures();
    this.circuitBreaker.closeCircuit();
    assertThat(this.circuitBreaker.isClosed())
        .withFailMessage("Should be CLOSED after explicit close circuit request")
        .isTrue();
  }

  @Test
  public void testClosedAfter1Failure() {
    LOGGER.info("Test: expect status is CLOSED after one failure");
    this.markFailure();
    assertThat(this.circuitBreaker.isClosed())
        .withFailMessage("Status should be CLOSED after 1 failure")
        .isTrue();
  }

  @Test
  public void testOpenAfter2Failures() {
    LOGGER.info("Test: expect status is OPEN after two consecutive failures");
    this.markTwoFailures();

    assertThat(this.circuitBreaker.isClosed())
        .withFailMessage("Status should be OPEN after 2 failures")
        .isFalse();
  }

  @Test
  public void testClosedAfter2FailuresAndWait() {
    LOGGER.info(
        "Test: expect status is CLOSED after two failures and waiting longer than the initial duration");
    // Trigger the circuit breaker to open
    this.markTwoFailures();
    // Wait until the circuit breaker is closed
    this.wait(35);

    assertThat(this.circuitBreaker.isClosed())
        .withFailMessage("Status should be CLOSED after waiting for 35 ms")
        .isTrue();
  }

  @Test
  public void testDurationIncrease() {
    LOGGER.info(
        "Test: expect status is OPEN after waiting shorter than the current duration of 600 ms");
    this.circuitBreaker =
        new CircuitBreaker.Builder()
            .withThreshold(2)
            .withInitialDuration(30)
            .withMaximumDuration(1200)
            .withMultiplier(20)
            .build();

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

    assertThat(this.circuitBreaker.isClosed())
        .withFailMessage("Status should be OPEN after waiting for 100 ms")
        .isFalse();
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

    assertThat(this.circuitBreaker.isClosed())
        .withFailMessage("Status should be CLOSED after waiting for 125 ms")
        .isTrue();
  }

  @Test
  public void testClosedAfterFailingOnceAfterSuccess() {
    this.markFailure();
    // One success resets the threshold to 2.
    this.markSuccess();
    this.markFailure();

    assertThat(this.circuitBreaker.isClosed())
        .withFailMessage("Status should be CLOSED, when there's one failure after a success")
        .isTrue();
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
      fail("Sleep interrupted");
    }
  }
}
