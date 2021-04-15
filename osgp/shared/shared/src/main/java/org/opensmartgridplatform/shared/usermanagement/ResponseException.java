/*
 * Copyright 2014 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.usermanagement;

public class ResponseException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = -6152977924946851822L;

  public ResponseException(final String message) {
    super(message);
  }

  public ResponseException(final String message, final Throwable t) {
    super(message, t);
  }
}
