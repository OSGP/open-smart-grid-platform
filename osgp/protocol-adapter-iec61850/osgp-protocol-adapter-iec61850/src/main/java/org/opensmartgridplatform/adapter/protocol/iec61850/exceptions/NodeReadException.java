/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.exceptions;

import com.beanit.openiec61850.ServiceError;
import java.io.IOException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.ConnectionState;

/**
 * Thrown when a {@link ServiceError} or {@link IOException} is thrown by OpenMUC OpenIEC61850
 * during read actions.
 */
public class NodeReadException extends NodeException {

  /** Serial Version UID. */
  private static final long serialVersionUID = -2581601336545136801L;

  public NodeReadException(final String message) {
    super(message);
  }

  public NodeReadException(final String message, final Throwable throwable) {
    super(message, throwable);
  }

  public NodeReadException(
      final String message, final Throwable throwable, final ConnectionState connectionState) {
    super(message, throwable, connectionState);
  }
}
