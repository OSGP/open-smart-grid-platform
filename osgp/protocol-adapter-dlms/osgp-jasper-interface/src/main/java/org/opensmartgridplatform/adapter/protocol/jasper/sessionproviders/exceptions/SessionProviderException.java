// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.exceptions;

import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

public class SessionProviderException extends OsgpException {

  /** Serial Version UID. */
  private static final long serialVersionUID = -5449632007365870329L;

  public SessionProviderException(final String message) {
    super(ComponentType.PROTOCOL_DLMS, message);
  }

  public SessionProviderException(final String message, final Throwable throwable) {
    super(ComponentType.PROTOCOL_DLMS, message, throwable);
  }
}
