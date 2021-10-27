/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.exception;

public class SimulatorRuntimeException extends RuntimeException {
  /** Serial Version UID. */
  private static final long serialVersionUID = 3151698143549959719L;

  public SimulatorRuntimeException(final String message) {
    super(message);
  }

  public SimulatorRuntimeException(final String message, final Throwable throwable) {
    super(message, throwable);
  }

  public SimulatorRuntimeException(final InterruptedException e) {
    super(e);
  }
}
