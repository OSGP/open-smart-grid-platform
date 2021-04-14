/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.shared.db.domain.exceptions;

public class SharedDbException extends Exception {

  /** */
  private static final long serialVersionUID = -6074924962793671015L;

  public SharedDbException(final String message) {
    super(message);
  }

  public SharedDbException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
