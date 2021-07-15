/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.jasper.exceptions;

public class OsgpJasperException extends Exception {
  /** Serial Version UID. */
  private static final long serialVersionUID = 4296034756159213906L;

  public OsgpJasperException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
