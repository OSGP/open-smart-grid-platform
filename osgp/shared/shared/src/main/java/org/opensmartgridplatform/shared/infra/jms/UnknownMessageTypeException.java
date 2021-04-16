/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.jms;

public class UnknownMessageTypeException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = -8205251907838224642L;

  public UnknownMessageTypeException(final String message) {
    super(message);
  }

  public UnknownMessageTypeException(final String message, final Exception exception) {
    super(message, exception);
  }
}
