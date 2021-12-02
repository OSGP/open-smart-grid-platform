/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

public class MissingExecutorException extends ProtocolAdapterException
    implements NonRetryableException {

  private static final long serialVersionUID = -4724576956215552014L;

  public MissingExecutorException(final String message) {
    super(message);
  }
}
