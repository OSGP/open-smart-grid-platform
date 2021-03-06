/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.db.api.iec61850.exceptions;

public class Iec61850CoreDbApiException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = -589626721908058277L;

  public Iec61850CoreDbApiException(final String message) {
    super(message);
  }

  public Iec61850CoreDbApiException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
