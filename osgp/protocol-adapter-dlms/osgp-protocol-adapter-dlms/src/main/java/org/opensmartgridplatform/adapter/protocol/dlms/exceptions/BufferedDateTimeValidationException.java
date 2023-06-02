//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

public class BufferedDateTimeValidationException extends Exception {

  private static final long serialVersionUID = 600226866180972745L;

  public BufferedDateTimeValidationException(final String message) {
    super(message);
  }
}
