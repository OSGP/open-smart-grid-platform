/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.ws;

import java.io.IOException;
import org.opensmartgridplatform.shared.exceptionhandling.CircuitBreakerOpenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;

public class CircuitBreakerInterceptor implements ClientInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerInterceptor.class);

  private final CircuitBreaker circuitBreaker;

  public CircuitBreakerInterceptor(final CircuitBreaker circuitBreaker) {
    this.circuitBreaker = circuitBreaker;
  }

  @Override
  public boolean handleRequest(final MessageContext messageContext) {
    if (this.circuitBreaker.isClosed()) {
      LOGGER.debug("Circuit is closed, send request");
      return true;
    } else {
      final String message = "Message will not be sent, because circuit breaker is open.";
      LOGGER.debug(message);
      throw new CircuitBreakerOpenException(message);
    }
  }

  @Override
  public boolean handleResponse(final MessageContext messageContext) {
    LOGGER.debug("Message successfully handled.");
    this.circuitBreaker.markSuccess();

    return true;
  }

  @Override
  public boolean handleFault(final MessageContext messageContext) {
    LOGGER.debug(
        "A fault was returned. Indicate success to the circuit breaker, because it's not a communication problem.");
    this.circuitBreaker.markSuccess();

    return true;
  }

  @Override
  public void afterCompletion(final MessageContext messageContext, final Exception ex) {
    LOGGER.debug("In after completion.");
    if (ex instanceof IOException) {
      // A communication error occurred, the destination might be down
      LOGGER.warn("An IOException occurred, indicate failure to the circuit breaker.");
      this.circuitBreaker.markFailure();
    }
  }
}
