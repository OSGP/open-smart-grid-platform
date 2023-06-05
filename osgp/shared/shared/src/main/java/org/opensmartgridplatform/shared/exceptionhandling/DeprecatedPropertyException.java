// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.exceptionhandling;

public class DeprecatedPropertyException extends RuntimeException {

  /** Serial Version UID. */
  private static final long serialVersionUID = 5003641861132609212L;

  public DeprecatedPropertyException(final String message) {
    super(message);
  }
}
