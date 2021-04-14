/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdevicesimulator.exceptions;

public class DeviceSimulatorException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = -7903758769242215843L;

  public DeviceSimulatorException(final String message) {
    super(message);
  }

  public DeviceSimulatorException(final String message, final Exception e) {
    super(message, e);
  }
}
