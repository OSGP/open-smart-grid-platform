/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.springframework.stereotype.Component;

/**
 * OsgpExceptionConverter
 *
 * <p>Converts given exception to a OsgpException type, and removes cause exceptions from any other
 * type. This is because other layers need to deserialize the exception (and the cause within it)
 * and the Exception class must be known to these layers.
 */
@Component
public class OsgpExceptionConverter {

  /**
   * If the Exception is a OsgpException, this exception is returned.
   *
   * <p>If the Exception is not an OsgpException, only the exception message will be wrapped in an
   * TechnicalException (OsgpException subclass) and returned. This also applies to the cause when
   * it is an OsgpException.
   *
   * @param e The exception.
   * @return OsgpException the given exception or a new TechnicalException instance.
   */
  public OsgpException ensureOsgpOrTechnicalException(final Exception e) {

    final boolean osgpExceptionNotSupportedByShared =
        e instanceof ImageTransferException || e instanceof ProtocolAdapterException;

    if (e instanceof OsgpException && !osgpExceptionNotSupportedByShared) {
      return (OsgpException) e;
    }
    if (e instanceof ConnectionException) {
      return new FunctionalException(
          FunctionalExceptionType.CONNECTION_ERROR,
          ComponentType.PROTOCOL_DLMS,
          new OsgpException(ComponentType.PROTOCOL_DLMS, e.getMessage()));
    }

    return new TechnicalException(
        ComponentType.PROTOCOL_DLMS,
        "Unexpected exception while handling protocol request/response message",
        new OsgpException(ComponentType.PROTOCOL_DLMS, e.getMessage()));
  }
}
