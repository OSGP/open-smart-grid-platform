/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.exceptions;

public class ProtocolAdapterException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = 916943696172403469L;

  public ProtocolAdapterException(final String message) {
    super(message);
  }

  public ProtocolAdapterException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
