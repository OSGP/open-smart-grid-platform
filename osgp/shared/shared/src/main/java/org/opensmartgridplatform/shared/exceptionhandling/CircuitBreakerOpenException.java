// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
