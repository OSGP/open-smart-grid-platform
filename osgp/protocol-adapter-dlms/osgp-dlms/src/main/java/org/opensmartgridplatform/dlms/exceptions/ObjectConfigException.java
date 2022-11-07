/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */

package org.opensmartgridplatform.dlms.exceptions;

import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

public class ObjectConfigException extends OsgpException {
  private static final long serialVersionUID = -4298521754920856706L;

  public ObjectConfigException(final String message) {
    super(ComponentType.PROTOCOL_DLMS, message);
  }

  public ObjectConfigException(final String message, final Throwable throwable) {
    super(ComponentType.PROTOCOL_DLMS, message, throwable);
  }
}
