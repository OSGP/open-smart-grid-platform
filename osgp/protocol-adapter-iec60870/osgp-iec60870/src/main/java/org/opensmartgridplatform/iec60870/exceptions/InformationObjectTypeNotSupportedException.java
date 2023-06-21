// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.iec60870.exceptions;

public class InformationObjectTypeNotSupportedException extends RuntimeException {

  public InformationObjectTypeNotSupportedException() {
    super();
  }

  public InformationObjectTypeNotSupportedException(final String message) {
    super(message);
  }
}
