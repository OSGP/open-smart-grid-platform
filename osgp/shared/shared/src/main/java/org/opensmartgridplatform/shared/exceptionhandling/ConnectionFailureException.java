// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.exceptionhandling;

/** Exception used when the adapter can't connect to a device */
public class ConnectionFailureException extends ProtocolAdapterException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 6225973923992193514L;

  public ConnectionFailureException(final ComponentType componentType, final String message) {
    super(componentType, message);
  }
}
