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

import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

public class ConnectionTaskException extends RuntimeException {

  private static final long serialVersionUID = -5709055250101626431L;
  private final OsgpException osgpException;

  public ConnectionTaskException(final OsgpException osgpException) {
    this.osgpException = osgpException;
  }

  public OsgpException getOsgpException() {
    return this.osgpException;
  }
}
