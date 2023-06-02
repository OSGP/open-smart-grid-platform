//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.exceptions;

public class MissingExecutorException extends ProtocolAdapterException
    implements NonRetryableException {

  private static final long serialVersionUID = -4724576956215552014L;

  public MissingExecutorException(final String message) {
    super(message);
  }
}
