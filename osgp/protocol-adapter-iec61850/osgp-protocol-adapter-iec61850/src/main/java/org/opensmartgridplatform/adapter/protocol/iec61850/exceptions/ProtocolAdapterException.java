// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
