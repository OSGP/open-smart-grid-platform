/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.networking;

@SuppressWarnings("serial")
public class UnrecognizedMessageDataException extends Exception {

  private static final String MESSAGE =
      "Data in DLMS Push Notification cannot be decoded. Reason: %1$s";

  public UnrecognizedMessageDataException(final String reason) {
    super(String.format(MESSAGE, reason));
  }

  public UnrecognizedMessageDataException(final String reason, final Throwable cause) {
    super(String.format(MESSAGE, reason), cause);
  }
}
