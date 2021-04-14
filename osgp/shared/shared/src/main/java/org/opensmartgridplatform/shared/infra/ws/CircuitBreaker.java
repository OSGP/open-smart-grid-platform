/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.ws;

import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Circuit breaker class, which can OPEN and CLOSE a circuit. The circuit is initially CLOSED.
 *
 * <p>After "threshold" number of failures, the circuit is set to OPEN for a certain duration. After
 * the duration, the circuit will CLOSE to indicate the process can make a new call. When the new
 * call succeeds, the threshold will be reset to its initial value. If the new call fails as well,
 * the circuit will be set to OPEN and the duration will be multiplied with a multiplier.
 *
 * <p>The duration for the OPEN status is bound to a maximum. When a duration is multiplied, it will
 * be maximized to that maximum.
 *
 * <p>Use: a process can use the circuit breaker to track the status of a service: A CLOSED circuit
 * indicates the service is running. When the service is down, the circuit will be set to OPEN. This
 * can prevent making (many) calls to a service, which is known to be unreachable.
 */
public class CircuitBreaker {

  private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreaker.class);

  private enum Status {
    OPEN,
    HALF_OPEN,
    CLOSED
  }

  private Status status = Status.CLOSED;

  private final int threshold;
  private final int initialDuration;
  private final int maximumDuration;
  private final int multiplier;

  private int currentDuration;
  private int countDown;
  private Instant closeCircuitInstant;

  private final Lock lock;

  public static class Builder {
    private static final String NEGATIVE_VALUE_ERROR_MESSAGE =
        "Value %d is illegal, because negative values for %s are not allowed";

    private int threshold = 3;
    private int initialDuration = 15000;
    private int maximumDuration = 300000;
    private int multiplier = 2;

    public Builder() {
      // Empty constructor
    }

    /**
     * Sets the threshold.
     *
     * @param threshold Number of failures before the circuit breaker opens.
     * @return the updated builder.
     */
    public Builder withThreshold(final int threshold) {
      this.checkValueNotNegative(threshold, "threshold");

      this.threshold = threshold;
      return this;
    }

    /**
     * Sets the initial duration.
     *
     * @param initialDuration Number of milliseconds after which an open circuit breaker will
     *     automatically close.
     * @return the updated builder.
     */
    public Builder withInitialDuration(final int initialDuration) {
      this.checkValueNotNegative(initialDuration, "initialDuration");

      this.initialDuration = initialDuration;
      return this;
    }

    /**
     * Sets the maximum duration.
     *
     * @param maximumDuration The maximum time in milliseconds during which the circuit breaker is
     *     open.
     * @return the updated builder.
     */
    public Builder withMaximumDuration(final int maximumDuration) {
      this.checkValueNotNegative(maximumDuration, "maximumDuration");

      this.maximumDuration = maximumDuration;
      return this;
    }

    /**
     * Sets the duration multiplier.
     *
     * @param multiplier The multiplier for the current duration, when a call fails while the
     *     circuit breaker is half open.
     * @return the updated builder.
     */
    public Builder withMultiplier(final int multiplier) {
      this.checkValueNotNegative(multiplier, "multiplier");

      this.multiplier = multiplier;
      return this;
    }

    private void checkValueNotNegative(final int value, final String fieldName) {
      if (value < 0) {
        throw new IllegalArgumentException(
            String.format(NEGATIVE_VALUE_ERROR_MESSAGE, value, fieldName));
      }
    }

    public CircuitBreaker build() {
      return new CircuitBreaker(this);
    }
  }

  /**
   * Creates a closed circuit breaker.
   *
   * @param builder Contains the threshold, initial duration, maximum duration and multiplier to
   *     set.
   */
  private CircuitBreaker(final Builder builder) {
    this.threshold = builder.threshold;
    this.initialDuration = builder.initialDuration;
    this.maximumDuration = builder.maximumDuration;
    this.multiplier = builder.multiplier;

    this.countDown = this.threshold;
    this.currentDuration = this.initialDuration;

    this.lock = new ReentrantLock();
  }

  /** Opens the circuit. */
  public void openCircuit() {

    this.lock.lock();
    try {
      if (this.status != Status.OPEN) {
        LOGGER.warn("OPEN circuit, which is currently {}", this.status);
        this.countDown = 0;

        if (this.status == Status.HALF_OPEN) {
          this.currentDuration =
              Math.min(this.currentDuration * this.multiplier, this.maximumDuration);
        }

        LOGGER.info(
            "Period during which the circuit breaker will be open in milliseconds: {}",
            this.currentDuration);
        this.status = Status.OPEN;
        this.closeCircuitInstant = Instant.now().plusMillis(this.currentDuration);
      }
    } finally {
      this.lock.unlock();
    }
  }

  /** Closes the circuit and resets the failure countdown to its initial threshold value. */
  public void closeCircuit() {
    this.lock.lock();
    try {
      this.countDown = this.threshold;

      if (this.status != Status.CLOSED) {
        LOGGER.info("Close circuit");
        this.currentDuration = this.initialDuration;
        this.status = Status.CLOSED;
        this.closeCircuitInstant = null;
      }
    } finally {
      this.lock.unlock();
    }
  }

  /**
   * Half opens the circuit. This is an internal intermediate state. If the next call fails, the
   * circuit will open. If the next call succeeds, the circuit will close.
   */
  private void halfOpenCircuit() {
    this.lock.lock();
    try {
      if (this.status != Status.CLOSED) {
        LOGGER.warn("Set circuit, which is currently {}, to HALF_OPEN", this.status);
        this.status = Status.HALF_OPEN;
        this.countDown = 1;
        this.closeCircuitInstant = null;
      } else {
        throw new IllegalStateException("It's not allowed to transition from CLOSED to HALF_OPEN");
      }
    } finally {
      this.lock.unlock();
    }
  }

  public boolean isClosed() {
    final Status currentStatus = this.updateAndGetStatus();

    return currentStatus != Status.OPEN;
  }

  private Status updateAndGetStatus() {
    this.lock.lock();
    try {

      if (this.status == Status.OPEN && Instant.now().isAfter(this.closeCircuitInstant)) {
        LOGGER.info("Timeout expired for OPEN circuit");
        this.halfOpenCircuit();
      }

      return this.status;
    } finally {
      this.lock.unlock();
    }
  }

  public void markSuccess() {
    this.closeCircuit();
  }

  public void markFailure() {
    final boolean isCurrentlyClosed = this.isClosed();
    if (isCurrentlyClosed) {
      this.processFailureWhenClosed();
    } else {
      LOGGER.debug("New failure. No action needed, circuit already open.");
    }
  }

  private void processFailureWhenClosed() {
    this.lock.lock();
    try {
      this.countDown--;
      LOGGER.warn(
          "Failure occurred. Decreased countDown value for circuit breaker to {}", this.countDown);
      if (this.countDown == 0) {
        this.openCircuit();
      }
    } finally {
      this.lock.unlock();
    }
  }
}
