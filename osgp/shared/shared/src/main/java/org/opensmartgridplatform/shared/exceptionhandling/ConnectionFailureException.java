/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.exceptionhandling;

/** Exception used when the adapter can't connect to a device */
public class ConnectionFailureException extends ProtocolAdapterException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 6225973923992193514L;

  public ConnectionFailureException(final ComponentType componentType, final String message) {
    super(componentType, message);
  }
}
