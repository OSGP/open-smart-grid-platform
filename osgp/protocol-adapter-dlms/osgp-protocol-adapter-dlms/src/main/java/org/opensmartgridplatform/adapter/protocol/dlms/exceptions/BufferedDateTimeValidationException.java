/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

public class BufferedDateTimeValidationException extends Exception {

  private static final long serialVersionUID = 600226866180972745L;

  public BufferedDateTimeValidationException(final String message) {
    super(message);
  }
}
