// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.exceptionhandling;

/** Exception used when the adapter can't connect to a device */
public class ProtocolAdapterException extends OsgpException {

  /** Serial Version UID. */
  private static final long serialVersionUID = -5209047409800408680L;

  public ProtocolAdapterException(final ComponentType componentType, final String message) {
    super(componentType, message);
  }

  public ProtocolAdapterException(
      final ComponentType componentType, final String message, final Throwable cause) {
    super(componentType, message, cause);
  }
}
