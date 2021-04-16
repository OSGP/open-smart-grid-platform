/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.exceptions;

import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

public class ProtocolAdapterException extends OsgpException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 916943696172403469L;

  public ProtocolAdapterException(final String message) {
    super(ComponentType.PROTOCOL_IEC61850, message);
  }

  public ProtocolAdapterException(final String message, final Throwable throwable) {
    super(ComponentType.PROTOCOL_IEC61850, message, throwable);
  }
}
