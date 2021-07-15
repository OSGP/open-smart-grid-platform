/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

public class ImageTransferException extends OsgpException {

  private static final long serialVersionUID = -3723899623610781058L;

  public ImageTransferException(final String message, final Throwable cause) {
    super(ComponentType.PROTOCOL_DLMS, message, cause);
  }

  public ImageTransferException(final String message) {
    super(ComponentType.PROTOCOL_DLMS, message);
  }
}
