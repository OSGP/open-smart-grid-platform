// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.exceptions;

public class ProtocolAdapterException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = 916943696172403469L;

  public ProtocolAdapterException(final String message) {
    super(message);
  }

  public ProtocolAdapterException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
