/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.exceptionhandling;

/** Exception used when the adapter can't connect to a device */
public class ProtocolAdapterException extends OsgpException {

  /** Serial Version UID. */
  private static final long serialVersionUID = -5209047409800408680L;

  public ProtocolAdapterException(final ComponentType componentType, final String message) {
    super(componentType, message);
  }

  public ProtocolAdapterException(
      final ComponentType componentType, final String message, final Throwable cause) {
    super(componentType, message, cause);
  }
}
