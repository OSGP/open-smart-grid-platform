// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

public class ConnectionTaskException extends RuntimeException {

  private static final long serialVersionUID = -5709055250101626431L;
  private final OsgpException osgpException;

  public ConnectionTaskException(final OsgpException osgpException) {
    super(osgpException);
    this.osgpException = osgpException;
  }

  public OsgpException getOsgpException() {
    return this.osgpException;
  }
}
