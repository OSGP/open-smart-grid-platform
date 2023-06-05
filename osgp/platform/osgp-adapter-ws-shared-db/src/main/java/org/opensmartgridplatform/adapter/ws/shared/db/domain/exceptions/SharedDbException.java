// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.shared.db.domain.exceptions;

public class SharedDbException extends Exception {

  /** */
  private static final long serialVersionUID = -6074924962793671015L;

  public SharedDbException(final String message) {
    super(message);
  }

  public SharedDbException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
