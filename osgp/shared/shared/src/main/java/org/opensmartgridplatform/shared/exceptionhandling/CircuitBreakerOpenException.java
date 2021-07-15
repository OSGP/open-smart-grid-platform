/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.exceptionhandling;

/**
 * Exception to indicate an attempt was made to send a message while the circuit breaker was open
 * for the message's destination.
 */
public class CircuitBreakerOpenException extends RuntimeException {

  private static final long serialVersionUID = -4433233488701660481L;

  public CircuitBreakerOpenException(final String message) {
    super(message);
  }
}
