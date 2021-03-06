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

public class NotSupportedByProtocolException extends ProtocolAdapterException
    implements NonRetryableException {

  private static final long serialVersionUID = -5950272388282811074L;

  public NotSupportedByProtocolException(final String message) {
    super(message);
  }
}
