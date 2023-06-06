// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.exceptionhandling;

public class OsgpException extends Exception {

  /** Serial Version UID. */
  private static final long serialVersionUID = 3985910152334024442L;

  protected final ComponentType componentType;

  public OsgpException(final ComponentType componentType, final String message) {
    super(message);
    this.componentType = componentType;
  }

  public OsgpException(
      final ComponentType componentType, final String message, final Throwable cause) {
    super(message, cause);
    this.componentType = componentType;
  }

  public ComponentType getComponentType() {
    return this.componentType;
  }
}
