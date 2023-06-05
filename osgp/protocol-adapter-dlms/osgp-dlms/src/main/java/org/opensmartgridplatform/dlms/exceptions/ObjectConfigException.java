// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dlms.exceptions;

public class ObjectConfigException extends Exception {
  private static final long serialVersionUID = -4298521754920856706L;

  public ObjectConfigException(final String message) {
    super(message);
  }

  public ObjectConfigException(final String message, final Throwable throwable) {
    super(message, throwable);
  }
}
