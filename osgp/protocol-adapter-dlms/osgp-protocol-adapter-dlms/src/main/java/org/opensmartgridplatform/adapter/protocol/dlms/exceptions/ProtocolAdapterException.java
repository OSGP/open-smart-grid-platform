//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

public class ProtocolAdapterException extends OsgpException {
  /** Serial Version UID. */
  private static final long serialVersionUID = 916943696172421469L;

  public ProtocolAdapterException(final String message) {
    super(ComponentType.PROTOCOL_DLMS, message);
  }

  public ProtocolAdapterException(final String message, final Throwable throwable) {
    super(ComponentType.PROTOCOL_DLMS, message, throwable);
  }
}
