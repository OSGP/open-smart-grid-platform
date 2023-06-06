// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.exceptionhandling;

public class NotSupportedException extends OsgpException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 6225973923998793514L;

  public NotSupportedException(final ComponentType componentType, final String message) {
    super(componentType, message);
  }
}
