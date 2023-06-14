// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.exceptions;

import com.beanit.openiec61850.ServiceError;
import java.io.IOException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.ConnectionState;

/**
 * Thrown when a {@link ServiceError} or {@link IOException} is thrown by OpenMUC OpenIEC61850
 * during read actions.
 */
public class NodeReadException extends NodeException {

  /** Serial Version UID. */
  private static final long serialVersionUID = -2581601336545136801L;

  public NodeReadException(final String message) {
    super(message);
  }

  public NodeReadException(final String message, final Throwable throwable) {
    super(message, throwable);
  }

  public NodeReadException(
      final String message, final Throwable throwable, final ConnectionState connectionState) {
    super(message, throwable, connectionState);
  }
}
